package pl.syntaxdevteam.cleanerx.eventhandler

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.base.SwearCounter

class CleanerXChat(
    private val plugin: CleanerX,
    private val wordFilter: WordFilter,
    private val fullCensorship: Boolean,
    private val swearCounter: SwearCounter
) : Listener {

    private val blockLinks = plugin.config.getBoolean("block-links", false)
    private val usePunishment = plugin.config.getBoolean("use-punishment", false)
    private val urlDetectors: List<UrlDetector> = listOf(
        UriUrlDetector(),
        RegexUrlDetector()
    )


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onChat(event: AsyncPlayerChatEvent) {
        try {
            var message: String = event.message

            // Blokowanie linków
            if (blockLinks && urlDetectors.any { it.containsUrl(message) }) {
                event.isCancelled = true
                event.player.sendMessage(
                    plugin.messageHandler.stringMessageToString("error", "no-link")
                )
                return
            }

            // Sprawdzanie słów zakazanych
            var swearWordCount = 0
            val words = message.split("\\s+".toRegex())
            for (word in words) {
                if (wordFilter.containsBannedWord(word)) {
                    swearWordCount++
                }
            }

            // Cenzura i kara
            if (swearWordCount > 0) {
                message = wordFilter.censorMessage(message, fullCensorship)
                event.message = message
                if (usePunishment) {
                    swearCounter.incrementSwearCount(event.player, swearWordCount)
                }
            }

        } catch (e: Exception) {
            plugin.logger.severe("Critical error! Send a message to the plugin author with the subject \"Error in onChat\" and the content: ${e.message}")
            e.printStackTrace()
        }
    }
}

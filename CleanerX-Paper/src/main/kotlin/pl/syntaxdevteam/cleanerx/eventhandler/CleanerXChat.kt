package pl.syntaxdevteam.cleanerx.eventhandler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.base.SwearCounter

/**
 * The `CleanerXChat` class is responsible for handling chat messages sent by players.
 * It listens for chat events and filters out inappropriate language and links from the chat.
 *
 * @property plugin The instance of the `CleanerX` plugin, used for logging messages and accessing other plugin functionalities.
 * @property wordFilter The word filter instance, used for checking and censoring inappropriate language.
 * @property fullCensorship A boolean value indicating whether the plugin should fully censor inappropriate language.
 * @property swearCounter The swear counter instance, used for tracking the number of swear words used by players.
 */
class CleanerXChat(
    private val plugin: CleanerX,
    private val wordFilter: WordFilter,
    private val fullCensorship: Boolean,
    private val swearCounter: SwearCounter
) : Listener {

    private val blockLinks = plugin.config.getBoolean("block-links", true)
    private val usePunishment = plugin.config.getBoolean("use-punishment", false)

    private val urlDetectors: List<UrlDetector> = listOf(
        UriUrlDetector(),
        RegexUrlDetector()
    )

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        try {
            val message = plugin.messageHandler.getPlainText(event.originalMessage())

            if (blockLinks && urlDetectors.any { it.containsUrl(message) }) {
                event.isCancelled = true
                event.player.sendMessage(
                    plugin.messageHandler.stringMessageToComponent("error", "no-link")
                )
                return
            }

            // dalej klasyczna cenzura wulgaryzmów
            val words = message.split("\\s+".toRegex())
            val swearCount = words.count { wordFilter.containsBannedWord(it) }

            if (swearCount > 0) {
                event.message(
                    Component.text(wordFilter.censorMessage(message, fullCensorship))
                )
                if (usePunishment) {
                    swearCounter.incrementSwearCount(event.player, swearCount)
                }
            }
        } catch (e: Exception) {
            plugin.logger.severe(
                "Critical error in onChat: ${e.message}"
            )
            e.printStackTrace()
        }
    }
}
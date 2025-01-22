package pl.syntaxdevteam.cleanerx.eventhandler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.base.SwearCounter

class CleanerXChat(
    private val plugin: CleanerX,
    private val wordFilter: WordFilter,
    private val fullCensorship: Boolean,
    private val swearCounter: SwearCounter
) : Listener {

    private val linkRegex = "(https?://\\S+|www\\.\\S+|\\b\\w+\\.(com|net|org|pl|co|gov|edu|info|biz|ru|uk|us|de|fr|cn|es|it|au|nl|ca|in|jp|se|ch|br|za|pt|tv|me|xyz|tech|online|store|site|live|app|io|ai|dev|ly|digital|agency|solutions|global|world|studio|cloud|media|network|works)\\b)".toRegex()
    private val blockLinks = plugin.config.getBoolean("block-links")
    private val usePunishment = plugin.config.getBoolean("use-punishment")
    private val noLink: String = plugin.config.getString("message.no-link") ?: "<red>Sharing links in the chat is not allowed on this server!"

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        try {
            val player = event.player
            val message: String = (event.originalMessage() as? TextComponent)?.content() ?: return

            if (blockLinks && linkRegex.containsMatchIn(message)) {
                event.isCancelled = true
                player.sendRichMessage(noLink)
                return
            }

            var swearWordCount = 0
            val words = message.split("\\s+".toRegex())
            for (word in words) {
                if (wordFilter.containsBannedWord(word)) {
                    swearWordCount++
                }
            }

            val censoredMessage = if (swearWordCount > 0) {
                wordFilter.censorMessage(message, fullCensorship)
            } else {
                message
            }

            val component = Component.text("${player.name}: $censoredMessage")

            Bukkit.getOnlinePlayers().forEach { recipient ->
                recipient.sendMessage(component)
            }

            if (swearWordCount > 0 && usePunishment) {
                swearCounter.incrementSwearCount(player, swearWordCount)
            }

            event.isCancelled = true
        } catch (e: Exception) {
            plugin.logger.severe("Error in onChat: ${e.message}")
            e.printStackTrace()
        }
    }
}

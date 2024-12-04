package pl.syntaxdevteam.cleanerx.eventhandler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
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

    private val linkRegex = "(https?://\\S+|www\\.\\S+|\\b\\w+\\.(com|net|org|pl|io|co|gov|edu|info|biz|ru|uk|us|de|fr|cn|es|it|au|nl|ca|in|jp|se|ch|br|za|pt|tv|me|xyz|tech|online|store|site|live|app|io|ai|dev|ly|digital|agency|solutions|global|world|studio|cloud|media|network|works)\\b)".toRegex()
    private val blockLinks = plugin.config.getBoolean("block-links")
    private val usePunishment = plugin.config.getBoolean("use-punishment")
    private val noLink: String = plugin.config.getString("message.no-link") ?: "<red>>Sharing links in the chat is not allowed on this server!"

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        try {
            val message: String = (event.originalMessage() as? TextComponent)?.content() ?: return

            if (blockLinks && linkRegex.containsMatchIn(message)) {
                event.isCancelled = true
                event.player.sendRichMessage(noLink)
                return
            }

            var swearWordCount = 0
            val words = message.split("\\s+".toRegex())
            for (word in words) {
                if (wordFilter.containsBannedWord(word)) {
                    swearWordCount++
                }
            }

            if (swearWordCount > 0) {
                event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
                if(usePunishment){
                    swearCounter.incrementSwearCount(event.player, swearWordCount)
                }
            }
        } catch (e: Exception) {
            plugin.logger.severe("Error in onChat, report it urgently to the plugin author with the message: ${e.message}")
            e.printStackTrace()
        }
    }

}

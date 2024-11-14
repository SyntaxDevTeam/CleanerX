package pl.syntaxdevteam.cleanerx.eventhandler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.base.SwearCounter

class CleanerXChat(
    private var plugin: CleanerX,
    private val wordFilter: WordFilter,
    private val fullCensorship: Boolean,
    private val swearCounter: SwearCounter
) : Listener {

    private val linkRegex = "(https?://\\S+|www\\.\\S+|\\b\\w+\\.(com|net|org|pl|io|co|gov|edu|info|biz)\\b)".toRegex()
    private val blockLinks = plugin.config.getBoolean("block-links")
    private val noLink: String = plugin.config.getString("message.no-link") ?: "<red>>Sharing links in the chat is not allowed on this server!"

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message: String = (event.originalMessage() as TextComponent).content()

        if (blockLinks && linkRegex.containsMatchIn(message)) {
            event.isCancelled = true
            event.player.sendRichMessage(noLink)
            return
        }

        if (wordFilter.containsBannedWord(message)) {
            event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
            if(blockLinks) {
                swearCounter.incrementSwearCount(event.player)
            }
        }
    }
}
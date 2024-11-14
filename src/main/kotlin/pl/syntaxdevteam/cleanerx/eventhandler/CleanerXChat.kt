package pl.syntaxdevteam.cleanerx.eventhandler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.cleanerx.base.WordFilter

class CleanerXChat(
    private val plugin: JavaPlugin,
    private val wordFilter: WordFilter,
    private val fullCensorship: Boolean
) : Listener {

    private val linkRegex = "(https?://\\S+|www\\.\\S+|\\b\\w+\\.(com|net|org|pl|io|co|gov|edu|info|biz)\\b)".toRegex()

    private val blockLinks: Boolean = plugin.config.getBoolean("block-links")

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message: String = (event.originalMessage() as TextComponent).content()

        if (blockLinks && linkRegex.containsMatchIn(message)) {
            event.isCancelled = true
            event.player.sendRichMessage("<red>>Sharing links in the chat is not allowed on this server!")
            return
        }

        if (wordFilter.containsBannedWord(message)) {
            event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
        }
    }
}

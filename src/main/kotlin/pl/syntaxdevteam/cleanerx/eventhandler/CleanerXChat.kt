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
    plugin: CleanerX,
    private val wordFilter: WordFilter,
    private val fullCensorship: Boolean,
    private val swearCounter: SwearCounter
) : Listener {

    private val linkRegex = "(https?://\\S+|www\\.\\S+|\\b\\w+\\.(com|net|org|pl|io|co|gov|edu|info|biz)\\b)".toRegex()
    private val blockLinks = plugin.config.getBoolean("block-links")

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val message = (event.originalMessage() as TextComponent).content()

        if (blockLinks && linkRegex.containsMatchIn(message)) {
            event.isCancelled = true
            player.sendMessage(Component.text("§cNie możesz wysyłać linków na czacie!"))
            return
        }

        if (wordFilter.containsBannedWord(message)) {
            event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
            swearCounter.incrementSwearCount(player)
        }
    }
}
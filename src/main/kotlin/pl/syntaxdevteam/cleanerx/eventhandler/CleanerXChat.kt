package pl.syntaxdevteam.cleanerx.eventhandler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pl.syntaxdevteam.cleanerx.base.WordFilter

class CleanerXChat(private val wordFilter: WordFilter, private val fullCensorship: Boolean) : Listener {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message: String = (event.originalMessage() as TextComponent).content()
        if (wordFilter.containsBannedWord(message)) {
            event.message(Component.text(wordFilter.censorMessage(message, fullCensorship)))
        }
    }
}

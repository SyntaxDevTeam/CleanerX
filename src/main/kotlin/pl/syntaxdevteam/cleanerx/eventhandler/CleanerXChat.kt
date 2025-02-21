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

    private val linkRegex = "(https?://\\S+|www\\.\\S+|\\b\\w+\\.(com|net|org|pl|co|gov|edu|info|biz|ru|uk|us|de|fr|cn|es|it|au|nl|ca|in|jp|se|ch|br|za|pt|tv|me|xyz|tech|online|store|site|live|app|io|ai|dev|ly|digital|agency|solutions|global|world|studio|cloud|media|network|works)\\b)".toRegex()
    private val blockLinks = plugin.config.getBoolean("block-links")
    private val usePunishment = plugin.config.getBoolean("use-punishment")

    /**
     * Event handler for chat messages.
     * This method is triggered when a player sends a chat message.
     * It cancels the original chat event and filters out inappropriate language and links from the message.
     *
     * @param event The `AsyncChatEvent` that contains information about the chat message.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        try {
            val message: String = (event.originalMessage() as? TextComponent)?.content() ?: return

            if (blockLinks && linkRegex.containsMatchIn(message)) {
                event.isCancelled = true
                event.player.sendMessage(plugin.messageHandler.getMessage("error", "no_link"))
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
            plugin.logger.severe("Critical error! Send a message to the plugin author with the subject \"Error in onChat\" and the content: ${e.message}")
            e.printStackTrace()
        }
    }

}

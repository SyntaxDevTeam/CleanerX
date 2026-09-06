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
        UriUrlDetector()
    )


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    fun onChat(event: AsyncPlayerChatEvent) {
        handleChat(event)
    }

    private fun handleChat(event: AsyncPlayerChatEvent) {
        try {
            if (shouldSkipCensorship(event.player)) {
                return
            }
            val message: String = event.message

            if (blockLinks && urlDetectors.any { it.containsUrl(message) }) {
                event.isCancelled = true
                val msg = plugin.messageHandler.legacyComponentSerializer(plugin.messageHandler.stringMessageToComponent("error", "no-link"))
                event.player.sendMessage(
                    msg
                )
                return
            }

            val words = message.split("\\s+".toRegex())
            val swearCount = words.count { wordFilter.containsBannedWord(it) }

            if (swearCount > 0) {
                event.message = wordFilter.censorMessage(message, fullCensorship)
                if (usePunishment) {
                    swearCounter.incrementSwearCount(event.player, swearCount)
                }
            }

        } catch (e: Exception) {
            plugin.logger.severe("Critical error! Send a message to the plugin author with the subject \"Error in onChat\" and the content: ${e.message}")
            plugin.logger.severe("[CLX-CHAT-001] Stacktrace: ${e.stackTraceToString()}")
            plugin.reportError(e)
        }
    }

    private fun shouldSkipCensorship(player: org.bukkit.entity.Player): Boolean {
        val punisherXApi = plugin.punisherXApi ?: return false

        return try {
            val uuid = player.uniqueId.toString()
            val isMuted = callPunisherXFlag(punisherXApi, "isMuted", uuid)
            val isJailed = callPunisherXFlag(punisherXApi, "isJailed", uuid)

            if (isMuted || isJailed) {
                plugin.logger.debug("Bypassing censor – ${player.name} is muted or in jail in by PunisherX.")
            }

            isMuted || isJailed
        } catch (exception: Exception) {
            plugin.logger.severe("Could not check penalty status for ${player.name}: ${exception.message}")
            plugin.reportError(exception)
            false
        }
    }

    private fun callPunisherXFlag(api: Any, methodName: String, uuid: String): Boolean {
        val method = api.javaClass.getMethod(methodName, String::class.java)
        val result = method.invoke(api, uuid) ?: return false

        return when (result) {
            is java.util.concurrent.CompletionStage<*> -> {
                result.toCompletableFuture().join() as? Boolean ?: false
            }
            is java.util.concurrent.Future<*> -> {
                result.get() as? Boolean ?: false
            }
            is Boolean -> result
            else -> false
        }
    }
}

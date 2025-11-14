package pl.syntaxdevteam.cleanerx.eventhandler

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pl.syntaxdevteam.cleanerx.CleanerX
import java.util.UUID

class PlayerJoinListener(private val plugin: CleanerX) : Listener {
    private val AUTHOR_UUID: UUID = UUID.fromString("248e508c-28de-4a8f-a284-2c73cf917d15")

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player    = event.player
        val name      = player.name
        val uuid      = player.uniqueId
        if (isAuthor(uuid)) {
            player.sendMessage(
                plugin.messageHandler
                    .formatMixedTextToMiniMessage(plugin.messageHandler.getPrefix() + " <green>Witaj, <b>$name!</b><newline>    Ten serwer używa ${plugin.pluginMeta.name} ${plugin.pluginMeta.version} ❤",
                        TagResolver.empty())
            )
        }
    }

    fun isAuthor(uuid: UUID): Boolean {
        return uuid == AUTHOR_UUID
    }
}
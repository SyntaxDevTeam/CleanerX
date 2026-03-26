package pl.syntaxdevteam.cleanerx.eventhandler

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import pl.syntaxdevteam.cleanerx.CleanerX
import java.util.UUID

class PlayerJoinListener(private val plugin: CleanerX) : Listener {
    private val authorUuid: UUID = UUID.fromString("248e508c-28de-4a8f-a284-2c73cf917d15")

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        val name = player.name
        val uuid = player.uniqueId

        if (isAuthor(uuid)) {
            val message = plugin.messageHandler.legacyComponentSerializer(
                plugin.messageHandler.formatMixedTextToMiniMessage(plugin.messageHandler.getPrefix() + " <green>Witaj, <b>$name</b><newline>    Ten serwer używa ${plugin.description.name} ${plugin.description.version} ❤"))
            player.sendMessage(message)
        }
    }

    fun isAuthor(uuid: UUID): Boolean {
        return uuid == authorUuid
    }
}

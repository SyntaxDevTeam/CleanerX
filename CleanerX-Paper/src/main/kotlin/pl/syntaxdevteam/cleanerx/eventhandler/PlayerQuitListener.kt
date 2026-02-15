package pl.syntaxdevteam.cleanerx.eventhandler

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import pl.syntaxdevteam.cleanerx.CleanerX

class PlayerQuitListener(private val plugin: CleanerX) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.swearCounter.resetSwearCount(event.player)
    }
}

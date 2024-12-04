package pl.syntaxdevteam.cleanerx.base

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import pl.syntaxdevteam.cleanerx.CleanerX

class SwearCounter(private val plugin: CleanerX) {
    private val playerSwearCounts = mutableMapOf<Player, Int>()
    private val thresholds = plugin.config.getConfigurationSection("swear-word-thresholds")?.getKeys(false)
        ?.associateBy({ it.toInt() }, { plugin.config.getString("swear-word-thresholds.$it")!! })
        ?: emptyMap()

    fun incrementSwearCount(player: Player, swearCount: Int) {
        val count = playerSwearCounts.getOrDefault(player, 0) + swearCount
        playerSwearCounts[player] = count

        thresholds.forEach { (threshold, command) ->
            if (count == threshold) {
                val formattedCommand = command.replace("{player}", player.name)
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    plugin.logger.debug("Attempting to send command: $formattedCommand")
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand)
                })

            }
        }
    }

    fun resetSwearCount(player: Player) {
        plugin.logger.debug("The words counted have been reset")
        playerSwearCounts.remove(player)
    }
}

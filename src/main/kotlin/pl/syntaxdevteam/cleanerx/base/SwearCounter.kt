package pl.syntaxdevteam.cleanerx.base

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import pl.syntaxdevteam.cleanerx.CleanerX

class SwearCounter(private val plugin: CleanerX) {
    private val playerSwearCounts = mutableMapOf<Player, Int>()
    private val thresholds = plugin.config.getConfigurationSection("swear-word-thresholds")?.getKeys(false)
        ?.associateBy({ it.toInt() }, { plugin.config.getString("swear-word-thresholds.$it")!! })
        ?: emptyMap()

    fun incrementSwearCount(player: Player) {
        val count = playerSwearCounts.getOrDefault(player, 0) + 1
        playerSwearCounts[player] = count

        thresholds.forEach { (threshold, command) ->
            if (count == threshold) {
                val formattedCommand = command.replace("{player}", player.name)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand)
            }
        }
    }

    fun resetSwearCount(player: Player) {
        playerSwearCounts.remove(player)
    }
}
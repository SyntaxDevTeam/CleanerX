package pl.syntaxdevteam.cleanerx.base

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import pl.syntaxdevteam.cleanerx.CleanerX

/**
 * SwearCounter is responsible for tracking the number of swear words used by players
 * and executing configured commands when certain thresholds are reached.
 *
 * @property plugin The instance of the CleanerX plugin.
 * @constructor Creates an instance of SwearCounter.
 */
class SwearCounter(private val plugin: CleanerX) {
    private val playerSwearCounts = mutableMapOf<Player, Int>()
    private val thresholds = plugin.config.getConfigurationSection("swear-word-thresholds")?.getKeys(false)
        ?.associateBy({ it.toInt() }, { plugin.config.getString("swear-word-thresholds.$it")!! })
        ?: emptyMap()

    /**
     * Increments the swear count for a given player by the specified amount.
     * If the new count matches any configured threshold, the corresponding command is executed.
     *
     * @param player The player whose swear count is to be incremented.
     * @param swearCount The number of swear words to add to the player's count.
     */
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

    /**
     * Resets the swear count for a given player.
     *
     * @param player The player whose swear count is to be reset.
     */
    fun resetSwearCount(player: Player) {
        plugin.logger.debug("The words counted have been reset")
        playerSwearCounts.remove(player)
    }
}

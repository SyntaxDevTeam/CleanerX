package pl.syntaxdevteam.cleanerx.base

import org.bukkit.entity.Player
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.util.SchedulerAdapter
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * SwearCounter is responsible for tracking the number of swear words used by players
 * and executing configured commands when certain thresholds are reached.
 *
 * @property plugin The instance of the CleanerX plugin.
 * @constructor Creates an instance of SwearCounter.
 */
class SwearCounter(private val plugin: CleanerX) {
    private val playerSwearCounts = ConcurrentHashMap<UUID, Int>()
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
        val playerId = player.uniqueId
        val count = playerSwearCounts.compute(playerId) { _, currentCount ->
            (currentCount ?: 0) + swearCount
        } ?: swearCount

        thresholds.forEach { (threshold, command) ->
            if (count == threshold) {
                val formattedCommand = command.replace("{player}", player.name)
                SchedulerAdapter.runSync(plugin) {
                    plugin.logger.debug("Attempting to send command: $formattedCommand")
                    plugin.server.dispatchCommand(plugin.server.consoleSender, formattedCommand)
                }

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
        playerSwearCounts.remove(player.uniqueId)
    }
}

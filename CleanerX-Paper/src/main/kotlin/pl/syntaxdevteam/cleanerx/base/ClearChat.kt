package pl.syntaxdevteam.cleanerx.base

import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * ClearChat is responsible for clearing the chat of all online players.
 *
 */
class ClearChat {

    fun clean() {
        val emptyLine = " ".repeat(100)

        Bukkit.getOnlinePlayers().forEach { player: Player ->
            repeat(100) {
                player.sendMessage(emptyLine)
            }
        }
    }
}
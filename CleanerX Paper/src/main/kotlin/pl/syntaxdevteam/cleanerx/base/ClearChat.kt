package pl.syntaxdevteam.cleanerx.base

import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
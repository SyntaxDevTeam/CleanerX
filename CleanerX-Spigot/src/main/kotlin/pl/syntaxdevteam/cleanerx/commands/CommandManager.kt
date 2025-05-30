package pl.syntaxdevteam.cleanerx.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import pl.syntaxdevteam.cleanerx.CleanerX

class CommandManager(private val plugin: CleanerX) {
    private val audiences: BukkitAudiences = BukkitAudiences.create(plugin)

    fun registerCommands() {
        val commands = mapOf(
            "cleanerx" to CleanerXCommand(plugin, audiences),
            "crx" to CleanerXCommand(plugin, audiences),
            "cleanx" to CleanCommand(plugin, audiences),
            "blacklistx" to BlackListCommand(plugin, audiences),
            "whitelistx" to WhiteListCommand(plugin, audiences)
        )

        commands.forEach { (name, executor) ->
            plugin.getCommand(name)?.setExecutor(executor)
            if (executor is org.bukkit.command.TabCompleter) {
                plugin.getCommand(name)?.tabCompleter = executor
            }
        }
    }

    fun shutdown() {
        audiences.close()
    }
}

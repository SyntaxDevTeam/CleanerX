package pl.syntaxdevteam.cleanerx.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pl.syntaxdevteam.cleanerx.CleanerX

class CleanerXCommand(private val plugin: CleanerX, private val audiences: BukkitAudiences) : CommandExecutor {
    private val pdf = plugin.description

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val audience = audiences.sender(sender)

        if (!sender.hasPermission("cleanerx.cmd.crx")) {
            audience.sendMessage(plugin.messageHandler.stringMessageToComponent("error", "no_permission"))
            return true
        }

        if (args.isEmpty()) {
            audience.sendMessage(plugin.messageHandler.stringMessageToComponent("error", "unknown_command"))
            return true
        }

        when (args[0].lowercase()) {
            "help" -> audience.sendMessage(MiniMessage.miniMessage().deserialize("""
                <gray>+-------------------------------------------------
                |
                |  <gold>Available commands for ${pdf.name}:
                <gray>|
                |  <gold>/crx help <gray>- <white>Displays this prompt.
                <gray>|  <gold>/blacklistx <add/remove/list> <word> 
                <gray>|                      <gray>- <white>Adds a word to the blacklist.
                <gray>|  <gold>/whitelistx <add/remove/list> <word> 
                <gray>|                      <gray>- <white>Adds a word to the whitelist.
                <gray>|  <gold>/cleanx <gray>- <white>Clears the chat window.
                <gray>|  <gold>/crx reset <player> <gray>- <white>Resets swear counter for player.
                <gray>|  <gold>/crx version <gray>- <white>Shows plugin info. 
                <gray>|  <gold>/crx reload <gray>- <white>Reloads the configuration file
                |
                <gray>+-------------------------------------------------
            """.trimIndent()))

            "version" -> audience.sendMessage(MiniMessage.miniMessage().deserialize("""
                <gray>-------------------------------------------------
                <gray>|
                <gray>|   <gold>→ <bold>${pdf.name}</bold> ←
                <gray>|   <white>Author: <bold><gold>${pdf.authors.joinToString()}</gold></bold>
                <gray>|   <white>Website: <bold><gold><click:open_url:'${pdf.website}'>${pdf.website}</click></gold></bold>
                <gray>|   <white>Version: <bold><gold>${pdf.version}</gold></bold>
                <gray>|
                <gray>-------------------------------------------------
            """.trimIndent()))

            "reload" -> {
                plugin.restartMyTask()
                audience.sendMessage(plugin.messageHandler.stringMessageToComponent("reload", "success"))
            }
            "reset" -> {
                if (args.size < 2) {
                    audience.sendMessage(
                        plugin.messageHandler.stringMessageToComponent(
                            "error",
                            "invalid_usage",
                            mapOf("usage" to "/crx reset <player>")
                        )
                    )
                    return true
                }
                val target = plugin.server.getPlayer(args[1])
                if (target == null) {
                    audience.sendMessage(
                        plugin.messageHandler.stringMessageToComponent(
                            "error",
                            "player_not_found",
                            mapOf("player" to args[1])
                        )
                    )
                    return true
                }
                plugin.swearCounter.resetSwearCount(target)
                audience.sendMessage(
                    plugin.messageHandler.stringMessageToComponent(
                        "swear_counter",
                        "reset_success",
                        mapOf("player" to target.name)
                    )
                )
            }

            else -> audience.sendMessage(plugin.messageHandler.stringMessageToComponent("error", "unknown_command"))
        }
        return true
    }
}

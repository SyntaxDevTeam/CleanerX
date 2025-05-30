package pl.syntaxdevteam.cleanerx.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pl.syntaxdevteam.cleanerx.CleanerX

class CleanerXCommand(private val plugin: CleanerX, private val audiences: BukkitAudiences) : CommandExecutor {
    private val mH = plugin.messageHandler
    private val pdf = plugin.description

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val audience = audiences.sender(sender)

        if (!sender.hasPermission("cleanerx.cmd.crx")) {
            audience.sendMessage(mH.getMessage("error", "no_permission"))
            return true
        }

        if (args.isEmpty()) {
            audience.sendMessage(mH.getMessage("error", "unknown_command"))
            return true
        }

        when (args[0].lowercase()) {
            "help" -> audience.sendMessage(mH.miniMessageFormat("""
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
                <gray>|  <gold>/crx version <gray>- <white>Shows plugin info. 
                <gray>|  <gold>/crx reload <gray>- <white>Reloads the configuration file
                |
                <gray>+-------------------------------------------------
            """.trimIndent()))

            "version" -> audience.sendMessage(mH.miniMessageFormat("""
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
                audience.sendMessage(mH.getMessage("reload", "success"))
            }

            else -> audience.sendMessage(mH.getMessage("error", "unknown_command"))
        }
        return true
    }
}

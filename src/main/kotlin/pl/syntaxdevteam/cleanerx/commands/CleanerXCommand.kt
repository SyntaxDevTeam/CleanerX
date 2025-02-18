package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage", "DEPRECATION")
class CleanerXCommand(private val plugin: CleanerX) : BasicCommand {
    private val mH = plugin.messageHandler

    override fun execute(stack: CommandSourceStack, args: Array<String>) {
        if (args.isEmpty()) {
            stack.sender.sendMessage(mH.miniMessageFormat("<green>Type /crx help to check available commands"))
            return
        }

        when (args[0].lowercase()) {
            "help" -> {
                if (!stack.sender.hasPermission("cleanerx.cmd.help")) {
                    stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                    return
                }
                stack.sender.sendMessage(
                    mH.miniMessageFormat(
                        """
                        <gray>+-------------------------------------------------
                        |  
                        |  <gold>Available commands for ${plugin.name}:
                        <gray>#
                        |  <gold>/crx help <gray>- <white>Displays this prompt.
                        <gray>|  <gold>/blacklistx <add/remove/list> <word> <gray>- <white>Adds a word to the blacklist.
                        <gray>|  <gold>/whitelistx <add/remove/list> <word> <gray>- <white>Adds a word to the whitelist.
                        <gray>|  <gold>/cleanx <gray>- <white>Clears the chat window.
                        <gray>|  <gold>/crx version <gray>- <white>Shows plugin info.
                        <gray>|  <gold>/crx reload <gray>- <white>Reloads the configuration file
                        |
                        <gray>+-------------------------------------------------
                        """.trimIndent()
                    )
                )
            }

            "version" -> {
                if (!stack.sender.hasPermission("cleanerx.cmd.version")) {
                    stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                    return
                }
                val pluginMeta = (plugin as LifecycleEventOwner).pluginMeta
                val pdf = plugin.description
                stack.sender.sendMessage(
                    mH.miniMessageFormat(
                        """
                        <gray>-------------------------------------------------
                        <gray>|
                        <gray>|   <gold>→ <bold>${pluginMeta.name}</bold> ←
                        <gray>|   <white>Author: <bold><gold>${pdf.authors}</gold></bold>
                        <gray>|   <white>Website: <bold><gold><click:open_url:'${pdf.website}'>${pdf.website}</click></gold></bold>
                        <gray>|   <white>Version: <bold><gold>${pluginMeta.version}</gold></bold>
                        <gray>|
                        <gray>-------------------------------------------------
                        """.trimIndent()
                    )
                )
            }

            "reload" -> {
                if (!stack.sender.hasPermission("cleanerx.cmd.reload")) {
                    stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                    return
                }
                plugin.restartMyTask()
                stack.sender.sendMessage(mH.getMessage("reload", "success"))
            }

            else -> {
                stack.sender.sendMessage(mH.getMessage("error", "unknown_command"))
            }
        }
    }

    override fun suggest(stack: CommandSourceStack, args: Array<String>): List<String> {
        return when (args.size) {
            1 -> listOf("help", "version", "reload")
            else -> emptyList()
        }
    }
}
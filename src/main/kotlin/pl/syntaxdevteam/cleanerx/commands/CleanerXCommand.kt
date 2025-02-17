package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage", "DEPRECATION")
class CleanerXCommand(private val plugin: CleanerX) : BasicCommand {
    private val mH = plugin.messageHandler

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        if (stack.sender.hasPermission("cleanerx.cmd.help") ||
            stack.sender.hasPermission("cleanerx.cmd.version") ||
            stack.sender.hasPermission("cleanerx.cmd.reload") ||
            stack.sender.hasPermission("cleanerx.help") ||
            stack.sender.hasPermission("cleanerx.version") ||
            stack.sender.hasPermission("cleanerx.reload")) {

            val pluginMeta = (plugin as LifecycleEventOwner).pluginMeta
            val pdf = plugin.description
            if (args.isNotEmpty()) {
                when {
                    args[0].equals("help", ignoreCase = true) -> {
                        if (stack.sender.hasPermission("cleanerx.cmd.help") || stack.sender.hasPermission("cleanerx.help")) {
                            stack.sender.sendMessage(
                                mH.miniMessageFormat(
                                    " <gray>+-------------------------------------------------\n" +
                                            " |\n" +
                                            " |  <gold>Available commands for " + pluginMeta.name + ":\n<gray>#\n" +
                                            " |  <gold>/crx help <gray>- <white>Displays this prompt.<gray>\n" +
                                            " <gray>|  <gold>/blacklistx <add/remove/list> <word> <gray>- <white>Adds a word to the blacklist.\n" +
                                            " <gray>|  <gold>/whitelistx <add/remove/list> <word> <gray>- <white>Adds a word to the whitelist.\n" +
                                            " <gray>|  <gold>/cleanx <gray>- <white>Clears the chat window.\n" +
                                            " <gray>|  <gold>/crx version <gray>- <white>Shows plugin info. \n<gray>" +
                                            " |  <gold>/crx reload <gray>- <white>Reloads the configuration file\n" +
                                            " |\n" +
                                            " <gray>|\n<gray>+-------------------------------------------------"
                                )
                            )
                        } else {
                            stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                        }
                    }

                    args[0].equals("version", ignoreCase = true) -> {
                        if (stack.sender.hasPermission("cleanerx.cmd.version") || stack.sender.hasPermission("cleanerx.version")) {
                            stack.sender.sendMessage(
                                mH.miniMessageFormat(
                                    "\n<gray>-------------------------------------------------\n" +
                                            " <gray>|\n" +
                                            " <gray>|   <gold>→ <bold>" + pluginMeta.name + "</bold> ←\n" +
                                            " <gray>|   <white>Author: <bold><gold>" + pdf.authors + "</gold></bold>\n" +
                                            " <gray>|   <white>Website: <bold><gold><click:open_url:'" + pdf.website + "'>" + pdf.website + "</click></gold></bold>\n" +
                                            " <gray>|   <white>Version: <bold><gold>" + pluginMeta.version + "</gold></bold>\n" +
                                            " <gray>|" +
                                            "\n-------------------------------------------------"
                                )
                            )
                        } else {
                            stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                        }
                    }

                    args[0].equals("reload", ignoreCase = true) -> {
                        if (stack.sender.hasPermission("cleanerx.cmd.reload") || stack.sender.hasPermission("cleanerx.reload")) {
                            plugin.restartMyTask()
                            stack.sender.sendMessage(mH.getMessage("reload", "success"))
                        } else {
                            stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
                        }
                    }

                    else -> {
                        stack.sender.sendMessage(mH.getMessage("error", "unknown_command"))
                    }
                }
            } else {
                stack.sender.sendMessage("<green>Type /crx help to check available commands")
            }
        }else{
            stack.sender.sendMessage(mH.getMessage("error", "no_permission"))
        }
    }
    override fun suggest(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>): List<String> {
        return when (args.size) {
            1 -> listOf("help", "version", "reload")
            else -> emptyList()
        }
    }
}

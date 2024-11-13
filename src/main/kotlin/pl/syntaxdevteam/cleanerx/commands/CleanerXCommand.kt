package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage", "DEPRECATION")
class CleanerXCommand(private val plugin: CleanerX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        val pluginMeta = (plugin as LifecycleEventOwner).pluginMeta
        val pdf = plugin.description
        if (args.isNotEmpty()) {
            when {
                args[0].equals("help", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.help")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#  <gold>Available commands for " + pluginMeta.name + ":\n<gray>#\n#  <gold>/crx help <gray>- <white>Displays this prompt.<gray>\n<gray>#  <gold>/crx addword <word> <gray>- <white>Adds a word to the blacklist.\n<gray>#  <gold>/crx version <gray>- <white>Shows plugin info. \n<gray>#  <gold>/crx reload <gray>- <white>Reloads the configuration file\n<gray>#\n#######################################")
                    } else {
                        stack.sender.sendMessage("You do not have permission to use this command.")
                    }
                }
                args[0].equals("version", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.version")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#   <gold>→ <bold>" + pluginMeta.name + "</bold> ←\n<gray>#   \n<gray>#   <white>Version: <bold><gold>" + pluginMeta.version + "(Paper/Folia)</gold></bold><white>Author: <bold><gold>" + pdf.authors + "</gold></bold>\n<gray>#   <white>Website: <bold><gold><click:open_url:'" + pdf.website + "'>"  + pdf.website + "</click></gold></bold><gray>\n#\n#######################################")
                    } else {
                        stack.sender.sendRichMessage("You do not have permission to use this command.")
                    }
                }
                args[0].equals("reload", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.reload")) {
                        plugin.restartMySentinelTask()
                        stack.sender.sendRichMessage("<green>The configuration file has been reloaded.</green>")
                    } else {
                        stack.sender.sendRichMessage("You do not have permission to use this command.")
                    }
                }
                args[0].equals("addword", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.addword")) {
                        if (args.size > 1) {
                            val newWord = args[1]
                            plugin.addBannedWord(newWord)
                            stack.sender.sendRichMessage("<green>New banned word added: $newWord</green>")
                        } else {
                            stack.sender.sendRichMessage("<red>Please provide a word to add.</red>")
                        }
                    } else {
                        stack.sender.sendMessage("You do not have permission to use this command.")
                    }
                }
                else -> {
                    stack.sender.sendRichMessage("<green>Type /crx help to check available commands")
                }
            }
        } else {
            stack.sender.sendRichMessage("<green>Type /crx help to check available commands")
        }
    }
    override fun suggest(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>): List<String> {
        return when (args.size) {
            1 -> listOf("help", "version", "reload", "addword")
            else -> emptyList()
        }
    }
}

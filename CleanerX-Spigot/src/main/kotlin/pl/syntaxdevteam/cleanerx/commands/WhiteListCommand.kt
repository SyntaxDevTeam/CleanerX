package pl.syntaxdevteam.cleanerx.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import pl.syntaxdevteam.cleanerx.CleanerX

class WhiteListCommand(private val plugin: CleanerX, private val audiences: BukkitAudiences) : CommandExecutor, TabCompleter {
    private val mH = plugin.messageHandler

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val audience = audiences.sender(sender)

        if (!sender.hasPermission("cleanerx.cmd.whitelist")) {
            audience.sendMessage(mH.getMessage("error", "no_permission"))
            return true
        }
        if (args.isEmpty()) {
            audience.sendMessage(mH.getMessage("whitelist", "usage"))
            return true
        }
        when (args[0].lowercase()) {
            "add" -> {
                if (args.size > 1) {
                    val newWord = args[1]
                    plugin.wordFilter.addWhitelistWord(newWord)
                    plugin.restartMyTask()
                    audience.sendMessage(mH.getMessage("word", "word_added", mapOf("word" to newWord)))
                } else {
                    audience.sendMessage(mH.getMessage("word", "no_word_provided"))
                }
            }
            "remove" -> {
                if (args.size > 1) {
                    val wordToRemove = args[1]
                    if (plugin.wordFilter.removeWhitelistWord(wordToRemove)) {
                        plugin.restartMyTask()
                        audience.sendMessage(mH.getMessage("word", "word_removed", mapOf("word" to wordToRemove)))
                    } else {
                        audience.sendMessage(mH.getMessage("word", "word_not_found", mapOf("word" to wordToRemove)))
                    }
                } else {
                    audience.sendMessage(mH.getMessage("word", "no_word_provided"))
                }
            }
            "list" -> {
                val whitelistWords = plugin.wordFilter.getWhitelistWords()
                val formattedList = whitelistWords.joinToString(", ")
                audience.sendMessage(MiniMessage.miniMessage().deserialize(mH.getPrefix() + " <green>Whitelist words: <white>$formattedList"))
            }
            else -> {
                audience.sendMessage(mH.getMessage("word", "usage"))
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (!sender.hasPermission("cleanerx.cmd.whitelist")) return emptyList()
        return when (args.size) {
            1 -> listOf("list", "add", "remove")
            else -> emptyList()
        }
    }
}

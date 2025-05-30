package pl.syntaxdevteam.cleanerx.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import pl.syntaxdevteam.cleanerx.CleanerX

class BlackListCommand(private val plugin: CleanerX, private val audiences: BukkitAudiences) : CommandExecutor, TabCompleter {
    private val mH = plugin.messageHandler

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("cleanerx.cmd.blacklist")) {
            sendMessage(sender, mH.getMessage("error", "no_permission"))
            return true
        }
        if (args.isEmpty()) {
            sendMessage(sender, mH.getMessage("word", "usage"))
            return true
        }
        when (args[0].lowercase()) {
            "add" -> {
                if (args.size > 1) {
                    val newWord = args[1]
                    plugin.wordFilter.addBannedWord(newWord)
                    plugin.restartMyTask()
                    sendMessage(sender, mH.getMessage("word", "word_added", mapOf("word" to newWord)))
                } else {
                    sendMessage(sender, mH.getMessage("word", "no_word_provided"))
                }
            }
            "remove" -> {
                if (args.size > 1) {
                    val wordToRemove = args[1]
                    if (plugin.wordFilter.removeBannedWord(wordToRemove)) {
                        plugin.restartMyTask()
                        sendMessage(sender, mH.getMessage("word", "word_removed", mapOf("word" to wordToRemove)))
                    } else {
                        sendMessage(sender, mH.getMessage("word", "word_not_found", mapOf("word" to wordToRemove)))
                    }
                } else {
                    sendMessage(sender, mH.getMessage("word", "no_word_provided"))
                }
            }
            "list" -> {
                val bannedWords = plugin.wordFilter.getBannedWords()
                val formattedList = bannedWords.joinToString(", ")
                sendMessage(sender, mH.miniMessageFormat(mH.getPrefix() + " <red>Banned words: <white>$formattedList"))
            }
            else -> {
                sendMessage(sender, mH.getMessage("word", "usage"))
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (!sender.hasPermission("cleanerx.cmd.blacklist")) {
            return emptyList()
        }
        return when (args.size) {
            1 -> listOf("list", "add", "remove")
            else -> emptyList()
        }
    }

    private fun sendMessage(sender: CommandSender, component: Component) {
        if (sender is org.bukkit.entity.Player) {
            audiences.player(sender).sendMessage(component)
        } else {
            sender.sendMessage(LegacyComponentSerializer.legacySection().serialize(component))
        }
    }
}

package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage")
class WhiteListCommand(private var plugin: CleanerX) : BasicCommand {
    private val mH = plugin.messageHandler

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {

        if (!stack.sender.hasPermission("cleanerx.cmd.whitelist")) {
            stack.sender.sendMessage(plugin.messageHandler.getMessage("error", "no_permission"))
            return
        }
        if (args.isEmpty()) {
            stack.sender.sendMessage(plugin.messageHandler.getMessage("word", "usage"))
            return
        }
        when (args[0].lowercase()) {
            "add" -> {
                if (args.size > 1) {
                    val newWord = args[1]
                    plugin.wordFilter.addWhitelistWord(newWord)
                    plugin.restartMyTask()
                    stack.sender.sendMessage(mH.getMessage("word", "word_added", mapOf("word" to newWord)))
                } else {
                    stack.sender.sendMessage(mH.getMessage("word", "no_word_provided"))
                }
            }
            "remove" -> {
                if (args.size > 1) {
                    val wordToRemove = args[1]
                    if(plugin.wordFilter.removeWhitelistWord(wordToRemove)) {
                        plugin.restartMyTask()
                        stack.sender.sendMessage(mH.getMessage("word", "word_removed", mapOf("word" to wordToRemove)))
                    }else{
                        stack.sender.sendMessage(mH.getMessage("word", "word_not_found", mapOf("word" to wordToRemove)))
                    }
                } else {
                    stack.sender.sendMessage(mH.getMessage("word", "no_word_provided"))
                }
            }
            "list" -> {
                val whitelistWords = plugin.wordFilter.getWhitelistWords()
                val formattedList = whitelistWords.joinToString(", ")
                stack.sender.sendMessage(mH.miniMessageFormat(mH.getPrefix() + " <green>Whitelist words: <white>$formattedList"))
            }
            else -> {
                stack.sender.sendMessage(plugin.messageHandler.getMessage("word", "usage"))
            }
        }
    }
    override fun suggest(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>): List<String> {
        return when (args.size) {
            1 -> listOf("list", "add", "remove")
            else -> emptyList()
        }
    }
}
package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage")
class WordCommand(private var plugin: CleanerX) : BasicCommand {
    private val mH = plugin.messageHandler

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {

        if (!stack.sender.hasPermission("cleanerx.cmd.word")) {
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
                    plugin.wordFilter.addBannedWord(newWord)
                    plugin.restartMyTask()
                    stack.sender.sendMessage(mH.getMessage("word", "word_added", mapOf("word" to newWord)))
                } else {
                    stack.sender.sendMessage(mH.getMessage("word", "no_word_provided"))
                }
            }
            "remove" -> {
                if (args.size > 1) {
                    val wordToRemove = args[1]
                    plugin.wordFilter.removeBannedWord(wordToRemove)
                    plugin.restartMyTask()
                    stack.sender.sendMessage(mH.getMessage("word", "word_removed", mapOf("word" to wordToRemove)))
                } else {
                    stack.sender.sendMessage(mH.getMessage("word", "no_word_provided"))
                }
            }
            "list" -> {
                val bannedWords = plugin.wordFilter.getBannedWords()
                val formattedList = bannedWords.joinToString(", ")
                stack.sender.sendMessage(mH.miniMessageFormat("<red>Banned words: <white>$formattedList"))
            }
            else -> {
                stack.sender.sendMessage(plugin.messageHandler.getMessage("word", "usage"))
            }
        }
    }
}
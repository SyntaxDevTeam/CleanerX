package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.ClearChat

@Suppress("UnstableApiUsage")
class CleanCommand(private val plugin: CleanerX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        if (stack.sender.hasPermission("CleanerX.clean")) {
            val clearChat = ClearChat()
            clearChat.clean()
            stack.sender.sendRichMessage("Chat has been cleared!")
            plugin.logger.log("<gold>Chat has been cleared!<gold>")
        } else {
            stack.sender.sendRichMessage("You do not have permission to use this command.")
        }
    }
}
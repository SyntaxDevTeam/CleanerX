package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.ClearChat

/**
 * Command that clears the chat.
 */
class CleanCommand(private val plugin: CleanerX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        if (stack.sender.hasPermission("cleanerx.cmd.clean")) {
            val player = stack.sender.name
            val clearChat = ClearChat()
            clearChat.clean()
            stack.sender.sendMessage(plugin.messageHandler.stringMessageToComponent("clean", "success"))
            plugin.logger.log(
                plugin.messageHandler.stringMessageToStringNoPrefix(
                    "clean",
                    "log",
                    mapOf("player" to player)
                )
            )
        } else {
            stack.sender.sendMessage(plugin.messageHandler.stringMessageToComponent("error", "no_permission"))
        }
    }
}
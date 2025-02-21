package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.ClearChat

/**
 * Command that clears the chat.
 */
@Suppress("UnstableApiUsage")
class CleanCommand(private val plugin: CleanerX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        if (stack.sender.hasPermission("cleanerx.cmd.clean")) {
            val player = stack.sender.name
            val clearChat = ClearChat()
            clearChat.clean()
            stack.sender.sendMessage(plugin.messageHandler.getMessage("clean", "success"))
            plugin.logger.log(plugin.messageHandler.getCleanMessage("clean", "log", mapOf("player" to player)))
        } else {
            stack.sender.sendMessage(plugin.messageHandler.getMessage("error", "no_permission"))
        }
    }
}
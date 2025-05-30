package pl.syntaxdevteam.cleanerx.commands

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.base.ClearChat

/**
 * Command that clears the chat.
 */
class CleanCommand(private val plugin: CleanerX, private val audiences: BukkitAudiences) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("cleanerx.cmd.clean")) {
            audiences.sender(sender).sendMessage(plugin.messageHandler.getMessage("error", "no_permission"))
            return true
        }

        val player = sender.name
        val clearChat = ClearChat()
        clearChat.clean()

        audiences.sender(sender).sendMessage(plugin.messageHandler.getMessage("clean", "success"))
        plugin.logger.info(plugin.messageHandler.getCleanMessage("clean", "log", mapOf("player" to player)))

        return true
    }
}

package pl.syntaxdevteam.cleanerx.commands

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage")
class CommandManager(private val plugin: CleanerX) {

    fun registerCommands() {
        val manager: LifecycleEventManager<Plugin> = plugin.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar()
            commands.register(
                "cleanerx",
                "CleanerX plugin command. Type /cleanerx help to check available commands",
                CleanerXCommand(plugin)
            )
            commands.register(
                "crx",
                "CleanerX plugin command. Type /crx help to check available commands",
                CleanerXCommand(plugin)
            )
            commands.register(
                "clean",
                "Clears the chat window. Type /clean to use commands",
                CleanCommand(plugin)
            )
            commands.register(
                "word",
                plugin.messageHandler.getSimpleMessage("word", "usage"),
                WordCommand(plugin)
            )
        }
    }
}
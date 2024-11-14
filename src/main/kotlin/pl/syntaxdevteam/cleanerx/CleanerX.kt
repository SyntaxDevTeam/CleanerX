package pl.syntaxdevteam.cleanerx

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.cleanerx.base.SwearCounter
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.commands.CleanCommand
import pl.syntaxdevteam.cleanerx.commands.CleanerXCommand
import pl.syntaxdevteam.cleanerx.eventhandler.*
import pl.syntaxdevteam.cleanerx.helpers.*
import java.io.File

@Suppress("UnstableApiUsage")
class CleanerX : JavaPlugin(), Listener {

    lateinit var logger: Logger
    val pluginMetas = this.pluginMeta
    private var config = getConfig()
    private var debugMode = config.getBoolean("debug")
    private lateinit var pluginManager: PluginManager
    private lateinit var statsCollector: StatsCollector
    private lateinit var updateChecker: UpdateChecker
    private val wordFilter = WordFilter(this)
    private val fullCensorship: Boolean = config.getBoolean("fullCensorship")
    private val swearCounter = SwearCounter(this)

    override fun onLoad() {
        logger = Logger(pluginMetas, debugMode)
    }

    override fun onEnable() {
        saveDefaultConfig()
        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar()
            commands.register("cleanerx", "CleanerX plugin command. Type /cleanerx help to check available commands", CleanerXCommand(this))
            commands.register("crx", "CleanerX plugin command. Type /crx help to check available commands", CleanerXCommand(this))
            commands.register("clean", "Clears the chat window.. Type /clean to use commands", CleanCommand(this))
        }
        server.pluginManager.registerEvents(CleanerXChat(this, wordFilter, fullCensorship, swearCounter), this)

        pluginManager = PluginManager(this)
        statsCollector = StatsCollector(this)
        updateChecker = UpdateChecker(this, pluginMetas, config)
        updateChecker.checkForUpdates()
    }

    fun restartMySentinelTask() {
        try {
            AsyncChatEvent.getHandlerList().unregister(this as Plugin)
            super.reloadConfig()
            updateSentinel()
        } catch (e: Exception) {
            logger.err("An error occurred while reloading the configuration: " + e.message)
        }
    }

    private fun updateSentinel() {
        server.pluginManager.registerEvents(CleanerXChat(this, wordFilter, fullCensorship, swearCounter), this)
    }

    fun addBannedWord(word: String) {
        wordFilter.addBannedWord(word)
        restartMySentinelTask()
    }

    fun getPluginFile(): File {
        return this.file
    }

    override fun onDisable() {
        AsyncChatEvent.getHandlerList().unregister(this as Listener)
        AsyncChatEvent.getHandlerList().unregister(this as Plugin)
    }
}
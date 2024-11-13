package pl.syntaxdevteam.cleanerx

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.commands.CleanCommand
import pl.syntaxdevteam.cleanerx.commands.CleanerXCommand
import pl.syntaxdevteam.cleanerx.eventhandler.*
import pl.syntaxdevteam.cleanerx.helpers.*
import java.io.File

@Suppress("UnstableApiUsage")
class CleanerX : JavaPlugin(), Listener {

    lateinit var logger: Logger
    private val pluginMetas = this.pluginMeta
    private var config = getConfig()
    private var debugMode = config.getBoolean("debug")
    private lateinit var wordFilter: WordFilter
    private var fullCensorship: Boolean = false
    private lateinit var pluginManager: PluginManager
    private lateinit var statsCollector: StatsCollector
    private lateinit var updateChecker: UpdateChecker

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
        this.wordFilter = WordFilter(this)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(CleanerXChat(wordFilter, fullCensorship), this)
        pluginManager = PluginManager(this)
        val externalPlugins = pluginManager.fetchPluginsFromExternalSource("https://raw.githubusercontent.com/SyntaxDevTeam/plugins-list/main/plugins.json")
        val loadedPlugins = pluginManager.fetchLoadedPlugins()
        val highestPriorityPlugin = pluginManager.getHighestPriorityPlugin(externalPlugins, loadedPlugins)
        if (highestPriorityPlugin == pluginMetas.name) {
            val syntaxDevTeamPlugins = loadedPlugins.filter { it.first != pluginMetas.name }
            logger.pluginStart(syntaxDevTeamPlugins)
        }
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
        this.wordFilter = WordFilter(this)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(CleanerXChat(wordFilter, fullCensorship), this)
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
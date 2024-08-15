package pl.syntaxdevteam

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.event.player.AsyncChatEvent
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.base.WordFilter
import pl.syntaxdevteam.commans.CleanerXCommand
import pl.syntaxdevteam.eventhandler.*
import pl.syntaxdevteam.helpers.*

@Suppress("UnstableApiUsage")
class CleanerX : JavaPlugin(), Listener {

    private lateinit var logger: Logger
    private val pluginMetas = this.pluginMeta
    private var config = getConfig()
    private var debugMode = config.getBoolean("debug")
    private lateinit var wordFilter: WordFilter
    private var fullCensorship: Boolean = false
    private lateinit var pluginManager: PluginManager


    override fun onLoad() {
        logger = Logger(pluginMetas, debugMode)
    }

    override fun onEnable() {
        saveDefaultConfig()

        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar()
            commands.register("CleanerX", "Komenda pluginu CleanerX. Wpisz /slx help aby sprawdzic dostępne komendy", CleanerXCommand(this))
            commands.register("crx", "Komenda pluginu CleanerX. Wpisz /CleanerX help aby sprawdzic dostępne komendy", CleanerXCommand(this))
        }
        this.wordFilter = WordFilter(this)
        this.fullCensorship = config.getBoolean("fullCensorship")
        server.pluginManager.registerEvents(CleanerXChat(wordFilter, fullCensorship), this)
        pluginManager = PluginManager(this)
        val externalPlugins = pluginManager.fetchPluginsFromExternalSource("https://raw.githubusercontent.com/SyntaxDevTeam/plugins-list/main/plugins.json")
        val loadedPlugins = pluginManager.fetchLoadedPlugins()
        val highestPriorityPlugin = pluginManager.getHighestPriorityPlugin(externalPlugins, loadedPlugins)
        if (highestPriorityPlugin == pluginMetas.name) {
            val syntaxDevTeamPlugins = loadedPlugins.filter { it != pluginMetas.name }
            logger.pluginStart(syntaxDevTeamPlugins)
        }
    }

    fun restartMySentinelTask() {
        try {
            AsyncChatEvent.getHandlerList().unregister(this as Plugin)
            super.reloadConfig()
            updateSentinel()
        } catch (e: Exception) {
            logger.err("Wystąpił błąd podczas przełądowania konfiguracji: " + e.message)
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

    override fun onDisable() {
        AsyncChatEvent.getHandlerList().unregister(this as Listener)
        AsyncChatEvent.getHandlerList().unregister(this as Plugin)
    }
}

package pl.syntaxdevteam.cleanerx.loader

import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.api.CleanerXAPI
import pl.syntaxdevteam.cleanerx.api.CleanerXApiImpl
import pl.syntaxdevteam.cleanerx.base.SwearCounter
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.commands.CommandManager
import pl.syntaxdevteam.cleanerx.common.BannedWordsSynchronizer
import pl.syntaxdevteam.cleanerx.common.ConfigHandler
import pl.syntaxdevteam.cleanerx.eventhandler.CleanerXChat
import pl.syntaxdevteam.core.SyntaxCore
import pl.syntaxdevteam.message.SyntaxMessages
import pl.syntaxdevteam.punisher.api.PunisherXApi

class PluginInitializer(private val plugin: CleanerX) {

    fun onEnable() {
        setUpLogger()
        setupConfig()
        setupHandlers()
        registerEvents()
        registerCommands()
        checkForUpdates()
    }

    fun onDisable() {
        plugin.logger.err(plugin.description.name + " " + plugin.description.version + " has been disabled ☹️")
    }

    private fun setUpLogger() {
        plugin.pluginConfig = plugin.config
        plugin.logger = SyntaxCore.logger
    }

    private fun setupConfig() {
        plugin.saveDefaultConfig()
        plugin.configHandler = ConfigHandler(plugin)
        plugin.configHandler.verifyAndUpdateConfig()
    }

    private fun setupHandlers() {
        SyntaxMessages.initialize(plugin)
        plugin.messageHandler = SyntaxMessages.messages
        plugin.versionChecker = VersionChecker(plugin)
        plugin.versionChecker.checkAndLog()
        plugin.wordFilter = WordFilter(plugin)
        plugin.swearCounter = SwearCounter(plugin)
        plugin.bannedWordsSynchronizer = BannedWordsSynchronizer(plugin)
        plugin.bannedWordsSynchronizer.synchronize { words ->
            plugin.wordFilter.updateBannedWords(words)
        }
        plugin.placeholderFix()

        plugin.pluginsManager = SyntaxCore.pluginManagerx
        hookPunisherX()
    }

    private fun registerCommands(){
        plugin.commandManager = CommandManager(plugin)
        plugin.commandManager.registerCommands()
    }

    fun registerEvents() {
        plugin.server.pluginManager.registerEvents(
            CleanerXChat(
                plugin,
                plugin.wordFilter,
                plugin.config.getBoolean("fullCensorship"),
                plugin.swearCounter
            ),
            plugin
        )
        resetAllSwearCounts()
        plugin.api = CleanerXApiImpl(plugin)
        plugin.server.servicesManager.register(CleanerXAPI::class.java, plugin.api, plugin, ServicePriority.Normal)
    }

    private fun hookPunisherX() {
        plugin.punisherXApi = plugin.server.servicesManager.load(PunisherXApi::class.java) ?: run {
            plugin.logger.severe("Nie znaleziono API PunisherX!")
            return
        }
        plugin.logger.info("Pobrano API PunisherX!")
    }

    private fun checkForUpdates() {
        plugin.statsCollector = SyntaxCore.statsCollector
        SyntaxCore.updateChecker.checkAsync()

    }

    fun resetAllSwearCounts() {
        for (player in Bukkit.getOnlinePlayers()) {
            plugin.swearCounter.resetSwearCount(player)
        }
    }
}

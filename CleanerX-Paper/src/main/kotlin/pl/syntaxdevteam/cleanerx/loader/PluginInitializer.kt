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
import pl.syntaxdevteam.cleanerx.eventhandler.PlayerJoinListener
import pl.syntaxdevteam.core.SyntaxCore
import pl.syntaxdevteam.message.SyntaxMessages

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
        plugin.logger.err(plugin.pluginMeta.name + " " + plugin.pluginMeta.version + " has been disabled ☹️")
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
        //plugin.messageHandler = SyntaxCore.messages
        SyntaxMessages.initialize(plugin)
        plugin.messageHandler = SyntaxMessages.messages
        plugin.pluginsManager = SyntaxCore.pluginManagerx
        plugin.versionChecker = VersionChecker(plugin)
        plugin.wordFilter = WordFilter(plugin)
        plugin.swearCounter = SwearCounter(plugin)
        plugin.bannedWordsSynchronizer = BannedWordsSynchronizer(plugin)
        plugin.bannedWordsSynchronizer.synchronize { words ->
            plugin.wordFilter.updateBannedWords(words)
        }
        hookPunisherX()
    }

    private fun registerCommands(){
        plugin.commandManager = CommandManager(plugin)
        plugin.commandManager.registerCommands()
    }

    fun registerEvents() {
        plugin.server.pluginManager.registerEvents(CleanerXChat(plugin, plugin.wordFilter, plugin.config.getBoolean("fullCensorship"), plugin.swearCounter), plugin)
        resetAllSwearCounts()
        plugin.api = CleanerXApiImpl(plugin)

        // Rejestracja API w Services Managerze
        plugin.server.servicesManager.register(CleanerXAPI::class.java, plugin.api, plugin, ServicePriority.Normal)
        plugin.server.pluginManager.registerEvents(PlayerJoinListener(plugin), plugin)
    }

    private fun hookPunisherX() {
        val punisherPlugin = plugin.server.pluginManager.getPlugin("PunisherX") ?: return
        if (!punisherPlugin.isEnabled) {
            return
        }

        val apiClass = try {
            Class.forName("pl.syntaxdevteam.punisher.api.PunisherXApi", false, punisherPlugin.javaClass.classLoader)
        } catch (exception: ClassNotFoundException) {
            plugin.logger.warning("PunisherX wykryty, ale brakuje API (PunisherXApi). Pomijam integrację.")
            return
        } catch (exception: Throwable) {
            plugin.logger.warning("PunisherX wykryty, ale API jest niedostępne (${exception.javaClass.simpleName}). Pomijam integrację.")
            return
        }

        plugin.punisherXApi = try {
            plugin.server.servicesManager.load(apiClass)
        } catch (exception: Throwable) {
            plugin.logger.warning("PunisherX wykryty, ale nie udało się pobrać API (${exception.javaClass.simpleName}). Pomijam integrację.")
            return
        } ?: run {
            plugin.logger.warning("PunisherX wykryty, ale nie udostępnia API. Pomijam integrację.")
            return
        }
        plugin.logger.info("Pobrano API PunisherX!")
    }

    private fun checkForUpdates() {
        plugin.statsCollector = SyntaxCore.statsCollector
        val updateChecker = runCatching { SyntaxCore.updateChecker }.getOrNull()
        if (updateChecker == null) {
            plugin.logger.warning("Update checker nie został zainicjalizowany w SyntaxCore. Pomijam sprawdzanie aktualizacji.")
            return
        }
        updateChecker.checkAsync()
    }

    fun resetAllSwearCounts() {
        for (player in Bukkit.getOnlinePlayers()) {
            plugin.swearCounter.resetSwearCount(player)
        }
    }
}

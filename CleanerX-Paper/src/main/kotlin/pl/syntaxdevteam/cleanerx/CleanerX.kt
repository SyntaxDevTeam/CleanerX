package pl.syntaxdevteam.cleanerx

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.cleanerx.api.CleanerXAPI
import pl.syntaxdevteam.cleanerx.base.SwearCounter
import pl.syntaxdevteam.cleanerx.base.WordFilter
import pl.syntaxdevteam.cleanerx.commands.CommandManager
import pl.syntaxdevteam.cleanerx.common.*
import pl.syntaxdevteam.cleanerx.loader.PluginInitializer
import pl.syntaxdevteam.cleanerx.loader.VersionChecker
import pl.syntaxdevteam.core.SyntaxCore
import pl.syntaxdevteam.core.manager.PluginManagerX
import pl.syntaxdevteam.core.logging.Logger
import pl.syntaxdevteam.core.stats.StatsCollector
import pl.syntaxdevteam.core.update.GitHubSource
import pl.syntaxdevteam.core.update.ModrinthSource
import pl.syntaxdevteam.message.MessageHandler
import java.io.File
import java.util.Locale

class CleanerX : JavaPlugin(), Listener {

    private lateinit var pluginInitializer: PluginInitializer

    lateinit var logger: Logger
    lateinit var messageHandler: MessageHandler
    lateinit var pluginsManager: PluginManagerX

    lateinit var configHandler: ConfigHandler
    lateinit var pluginConfig: FileConfiguration
    lateinit var statsCollector: StatsCollector

    lateinit var versionChecker: VersionChecker
    lateinit var wordFilter: WordFilter
    lateinit var swearCounter: SwearCounter
    lateinit var commandManager: CommandManager
    lateinit var api: CleanerXAPI
    lateinit var bannedWordsSynchronizer: BannedWordsSynchronizer
    var punisherXApi: Any? = null
    var lpcMode: Boolean = false

    override fun onEnable() {
        SyntaxCore.registerUpdateSources(
            GitHubSource("SyntaxDevTeam/CleanerX"),
            ModrinthSource("zJ4dsnYc")
        )
        SyntaxCore.init(this, versionType = "paper")
        pluginInitializer = PluginInitializer(this)
        pluginInitializer.onEnable()
        placeholderFix()
        versionChecker.checkAndLog()
    }

    override fun onDisable() {
        AsyncChatEvent.getHandlerList().unregister(this as Plugin)
        pluginInitializer.onDisable()
    }

    fun restartMyTask() {
        try {
            messageHandler.reloadMessages()
        } catch (e: Exception) {
            logger.err("${messageHandler.stringMessageToComponent("error", "reload")} ${e.message}")
        }

        saveDefaultConfig()
        reloadConfig()
        configHandler = ConfigHandler(this)
        configHandler.verifyAndUpdateConfig()

        try{
            HandlerList.unregisterAll(this as org.bukkit.plugin.Plugin)
            pluginInitializer.registerEvents()
        } catch (ee: Exception) {
            logger.err("An error occurred while reloading the configuration: " + ee.message)
        }
    }

    fun placeholderFix() {
        val lang = config.getString("language")?.lowercase(Locale.getDefault()) ?: "en"
        val langDir = File(dataFolder, "lang")
        val candidates = listOf(
            File(langDir, "messages_$lang.yml"),
            File(langDir, "message_$lang.yml")
        )
        val langFile = candidates.firstOrNull { it.exists() }

        if (langFile == null) {
            logger.err("Language file for $lang not found.")
            return
        }
        val prefix = messageHandler.getPrefix()
        try {
            val content = langFile.readText(Charsets.UTF_8)
            val updated = content.replace(Regex("\\{(\\w+)}"), "<$1>")
            langFile.writeText(updated, Charsets.UTF_8)

            logger.success("$prefix Converted placeholders in ${langFile.name}.")
        } catch (e: Exception) {
            logger.err("$prefix Failed to convert placeholders: ${e.message}")
        }
    }
}

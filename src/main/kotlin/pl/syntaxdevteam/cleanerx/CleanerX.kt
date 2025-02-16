package pl.syntaxdevteam.cleanerx

    import io.papermc.paper.event.player.AsyncChatEvent
    import org.bukkit.Bukkit
    import org.bukkit.configuration.file.FileConfiguration
    import org.bukkit.event.Listener
    import org.bukkit.plugin.Plugin
    import org.bukkit.plugin.java.JavaPlugin
    import pl.syntaxdevteam.cleanerx.base.SwearCounter
    import pl.syntaxdevteam.cleanerx.base.WordFilter
    import pl.syntaxdevteam.cleanerx.commands.CommandManager
    import pl.syntaxdevteam.cleanerx.common.*
    import pl.syntaxdevteam.cleanerx.eventhandler.*
    import java.io.File

    /**
     * The `CleanerX` class is the main class of the CleanerX plugin.
     * It is responsible for initializing and managing the plugin's components and functionalities.
     *
     * @property config The configuration file of the plugin.
     * @property logger The logger instance used for logging messages.
     * @property pluginsManager The manager for handling other plugins.
     * @property statsCollector The instance responsible for collecting statistics.
     * @property messageHandler The handler for managing messages.
     * @property updateChecker The instance responsible for checking for updates.
     * @property wordFilter The word filter instance used for filtering banned words.
     * @property fullCensorship A boolean value indicating whether full censorship is enabled.
     * @property swearCounter The swear counter instance used for tracking swear words.
     * @property commandManager The manager for handling plugin commands.
     */
    @Suppress("UnstableApiUsage")
    class CleanerX : JavaPlugin(), Listener {

        private val config: FileConfiguration = getConfig()
        var logger: Logger = Logger(this, config.getBoolean("debug"))
        lateinit var pluginsManager: PluginManager
        private lateinit var statsCollector: StatsCollector
        lateinit var messageHandler: MessageHandler
        private lateinit var updateChecker: UpdateChecker
        val wordFilter = WordFilter(this)
        private val fullCensorship: Boolean = config.getBoolean("fullCensorship")
        private val swearCounter = SwearCounter(this)
        private lateinit var commandManager: CommandManager
        private var configHandler = ConfigHandler(this)

        /**
         * Called when the plugin is enabled.
         * Initializes the plugin components and registers events and commands.
         */
        override fun onEnable() {
            setupConfig()
            setupHandlers()
            registerEvents()
            registerCommands()
            checkForUpdates()
            registerCommands()
            resetAllSwearCounts()
        }

        private fun setupConfig() {
            saveDefaultConfig()
            configHandler.verifyAndUpdateConfig()
        }

        private fun setupHandlers() {
            messageHandler = MessageHandler(this)
            pluginsManager = PluginManager(this)
        }

        /**
         * Registers the plugin commands.
         */
        private fun registerCommands(){
            commandManager = CommandManager(this)
            commandManager.registerCommands()
        }

        /**
         * Registers the plugin events.
         */
        private fun registerEvents() {
            server.pluginManager.registerEvents(CleanerXChat(this, wordFilter, fullCensorship, swearCounter), this)
        }

        /**
         * Checks for updates to the plugin.
         */
        private fun checkForUpdates() {
            statsCollector = StatsCollector(this)
            updateChecker = UpdateChecker(this)
            updateChecker.checkForUpdates()
        }

        /**
         * Reloads the plugin configuration and updates the CleanerXChat event handler.
         */
        fun restartMyTask() {
            try {
                AsyncChatEvent.getHandlerList().unregister(this as Listener)
                super.reloadConfig()
                updateCleanerX()
            } catch (e: Exception) {
                logger.err("An error occurred while reloading the configuration: " + e.message)
            }
        }

        /**
         * Re-registers the CleanerXChat event handler and resets all swear counts.
         */
        private fun updateCleanerX() {
            server.pluginManager.registerEvents(CleanerXChat(this, wordFilter, fullCensorship, swearCounter), this)
            resetAllSwearCounts()
        }

        /**
         * Returns the plugin file.
         *
         * @return The plugin file.
         */
        fun getPluginFile(): File {
            return this.file
        }

        /**
         * Called when the plugin is disabled.
         * Resets all swear counts and unregisters event handlers.
         */
        override fun onDisable() {
            resetAllSwearCounts()
            AsyncChatEvent.getHandlerList().unregister(this as Listener)
            AsyncChatEvent.getHandlerList().unregister(this as Plugin)
            logger.err(pluginMeta.name + " " + pluginMeta.version + " has been disabled ☹️")
        }

        /**
         * Resets the swear counts for all online players.
         */
        private fun resetAllSwearCounts() {
            for (player in Bukkit.getOnlinePlayers()) {
                swearCounter.resetSwearCount(player)
            }
        }
    }
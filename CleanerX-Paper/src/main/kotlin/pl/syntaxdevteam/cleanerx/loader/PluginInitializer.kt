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
import pl.syntaxdevteam.cleanerx.common.ListenerRegistrationMetrics
import pl.syntaxdevteam.cleanerx.eventhandler.CleanerXChat
import pl.syntaxdevteam.cleanerx.eventhandler.PlayerQuitListener
import pl.syntaxdevteam.cleanerx.eventhandler.PlayerJoinListener
import pl.syntaxdevteam.cleanerx.integration.rewriteFlectonePulseFormatting
import pl.syntaxdevteam.core.SyntaxCore
import pl.syntaxdevteam.message.SyntaxMessages
import java.util.function.UnaryOperator

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
        detectLpc()
        detectFlectonePulse()
        hookFlectonePulseFormatting()
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
                plugin.swearCounter,
                plugin.lpcMode,
                plugin.flectonePulseMode
            ),
            plugin
        )
        val registrationCount = ListenerRegistrationMetrics.increment()
        plugin.logger.info("Listener registration count: $registrationCount")

        resetAllSwearCounts()
        plugin.api = CleanerXApiImpl(plugin)

        // Rejestracja API w Services Managerze
        plugin.server.servicesManager.register(CleanerXAPI::class.java, plugin.api, plugin, ServicePriority.Normal)
        plugin.server.pluginManager.registerEvents(PlayerQuitListener(plugin), plugin)
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
            plugin.reportError(exception)
            return
        } catch (exception: Throwable) {
            plugin.logger.warning("PunisherX wykryty, ale API jest niedostępne (${exception.javaClass.simpleName}). Pomijam integrację.")
            plugin.reportError(exception)
            return
        }

        plugin.punisherXApi = try {
            plugin.server.servicesManager.load(apiClass)
        } catch (exception: Throwable) {
            plugin.logger.warning("PunisherX wykryty, ale nie udało się pobrać API (${exception.javaClass.simpleName}). Pomijam integrację.")
            plugin.reportError(exception)
            return
        } ?: run {
            plugin.logger.warning("PunisherX wykryty, ale nie udostępnia API. Pomijam integrację.")
            return
        }
        plugin.logger.info("Pobrano API PunisherX!")
    }

    private fun detectLpc() {
        val lpcPlugin = plugin.server.pluginManager.getPlugin("LPC")
        plugin.lpcMode = lpcPlugin != null && lpcPlugin.isEnabled
        if (plugin.lpcMode) {
            plugin.logger.info("LPC detected - enabling chat compatibility mode.")
        }
    }


    private fun detectFlectonePulse() {
        val flectonePulsePlugin = plugin.server.pluginManager.getPlugin("FlectonePulse")
        plugin.flectonePulseMode = flectonePulsePlugin != null && flectonePulsePlugin.isEnabled
        if (plugin.flectonePulseMode) {
            plugin.logger.info("FlectonePulse detected - enabling chat compatibility mode.")
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun hookFlectonePulseFormatting() {
        plugin.flectonePulseFormattingHooked = false
        if (!plugin.flectonePulseMode) return

        try {
            val flectonePlugin = plugin.server.pluginManager.getPlugin("FlectonePulse") ?: return
            val pulseClassLoader = flectonePlugin.javaClass.classLoader

            val pulseApiClass = Class.forName("net.flectone.pulse.FlectonePulseAPI", false, pulseClassLoader)
            val pulseInstance = pulseApiClass.getMethod("getInstance").invoke(null) ?: return
            val getMethod = pulseInstance.javaClass.getMethod("get", Class::class.java)

            val listenerRegistryClass = Class.forName("net.flectone.pulse.platform.registry.ListenerRegistry", false, pulseClassLoader)
            val messageFormattingEventClass = Class.forName("net.flectone.pulse.model.event.message.MessageFormattingEvent", false, pulseClassLoader)
            val messageContextClass = Class.forName("net.flectone.pulse.model.event.message.context.MessageContext", false, pulseClassLoader)
            val messageFlagClass = Class.forName("net.flectone.pulse.util.constant.MessageFlag", false, pulseClassLoader)
            val eventPriorityClass = Class.forName("net.flectone.pulse.model.event.Event\$Priority", false, pulseClassLoader)
            val lowestPriority = java.lang.Enum.valueOf(eventPriorityClass.asSubclass(Enum::class.java), "LOWEST")
            val playerMessageFlag = java.lang.Enum.valueOf(messageFlagClass.asSubclass(Enum::class.java), "PLAYER_MESSAGE")

            val listenerRegistry = getMethod.invoke(pulseInstance, listenerRegistryClass)
            val registerMethod = listenerRegistryClass.getMethod("register", Class::class.java, eventPriorityClass, UnaryOperator::class.java)
            val contextMethod = messageFormattingEventClass.getMethod("context")
            val withContextMethod = messageFormattingEventClass.getMethod("withContext", messageContextClass)
            val messageMethod = messageContextClass.getMethod("message")
            val userMessageMethod = messageContextClass.getMethod("userMessage")
            val isFlagMethod = messageContextClass.getMethod("isFlag", messageFlagClass)
            val withMessageMethod = messageContextClass.getMethod("withMessage", String::class.java)
            val withUserMessageMethod = messageContextClass.getMethod("withUserMessage", String::class.java)

            val formatterHook = UnaryOperator<Any> { event ->
                try {
                    val context = contextMethod.invoke(event) ?: return@UnaryOperator event
                    val rawMessage = messageMethod.invoke(context) as? String ?: ""
                    val userMessage = userMessageMethod.invoke(context) as? String ?: ""
                    val isPlayerMessage = isFlagMethod.invoke(context, playerMessageFlag) as? Boolean ?: false

                    val rewrite = rewriteFlectonePulseFormatting(
                        message = rawMessage,
                        userMessage = userMessage,
                        isPlayerMessage = isPlayerMessage,
                        fullCensorship = plugin.config.getBoolean("fullCensorship"),
                        censor = plugin.wordFilter::censorMessage
                    )

                    if (rewrite.changed) {
                        var updatedContext = context
                        if (rewrite.message != rawMessage) {
                            updatedContext = withMessageMethod.invoke(updatedContext, rewrite.message)
                        }
                        if (rewrite.userMessage != userMessage) {
                            updatedContext = withUserMessageMethod.invoke(updatedContext, rewrite.userMessage)
                        }
                        return@UnaryOperator withContextMethod.invoke(event, updatedContext)
                    }
                } catch (_: Throwable) {
                    return@UnaryOperator event
                }
                event
            }

            registerMethod.invoke(listenerRegistry, messageFormattingEventClass, lowestPriority, formatterHook)
            plugin.flectonePulseFormattingHooked = true
            plugin.logger.info("Zarejestrowano hak cenzury bezpośrednio do pipeline FlectonePulse (MessageFormattingEvent).")
        } catch (exception: Throwable) {
            plugin.logger.warning("Nie udało się podpiąć pod API FlectonePulse (${exception.javaClass.simpleName}).")
            plugin.reportError(exception)
        }
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

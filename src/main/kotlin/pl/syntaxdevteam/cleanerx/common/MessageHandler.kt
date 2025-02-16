@file:Suppress("DEPRECATION")

package pl.syntaxdevteam.cleanerx.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import pl.syntaxdevteam.cleanerx.CleanerX
import java.io.File

/**
 * The MessageHandler class manages messages for the FormatterX plugin.
 * It is responsible for loading, updating, and formatting messages from YAML files.
 * Version: 2.0.0
 *
 * @property plugin Instance of the main FormatterX plugin class.
 */
@Suppress("UnstableApiUsage", "unused")
class MessageHandler(private val plugin: CleanerX) {
    private val language = plugin.config.getString("language") ?: "EN"
    private var messages: FileConfiguration

    /**
     * Initializes default language settings and loads the message file.
     */
    init {
        copyDefaultMessages()
        updateLanguageFile()
        messages = loadMessages()
    }

    /**
     * Displays information about the language file author based on the selected language.
     */
    fun initial() {
        val author = when (language.lowercase()) {
            "pl" -> "WieszczY"
            "en" -> "Syntaxerr"
            else -> "OpenAI ChatGPT-4o"
        }
        plugin.logger.log("<gray>Loaded \"$language\" language file by: <white><b>$author</b></white>")
    }

    /**
     * Copies the default language file if it does not exist in the plugin's data folder.
     */
    private fun copyDefaultMessages() {
        val messageFile = File(plugin.dataFolder, "lang/messages_${language.lowercase()}.yml")
        if (!messageFile.exists()) {
            messageFile.parentFile.mkdirs()
            plugin.saveResource("lang/messages_${language.lowercase()}.yml", false)
        }
    }

    /**
     * Updates the language file by adding missing entries, synchronizing it with the default version
     * from the plugin's resources.
     */
    private fun updateLanguageFile() {
        val langFile = File(plugin.dataFolder, "lang/messages_${language.lowercase()}.yml")
        val defaultLangStream = plugin.getResource("lang/messages_${language.lowercase()}.yml")

        if (defaultLangStream == null) {
            plugin.logger.err("Default language file for $language not found in plugin resources!")
            return
        }

        val defaultConfig = YamlConfiguration.loadConfiguration(defaultLangStream.reader())
        val currentConfig = YamlConfiguration.loadConfiguration(langFile)

        var updated = false

        fun synchronizeSections(defaultSection: ConfigurationSection, currentSection: ConfigurationSection) {
            for (key in defaultSection.getKeys(false)) {
                if (!currentSection.contains(key)) {
                    currentSection[key] = defaultSection[key]
                    updated = true
                } else if (defaultSection.isConfigurationSection(key)) {
                    synchronizeSections(
                        defaultSection.getConfigurationSection(key)!!,
                        currentSection.getConfigurationSection(key)!!
                    )
                }
            }
        }

        synchronizeSections(defaultConfig, currentConfig)

        if (updated) {
            plugin.logger.success("Updating language file: messages_${language.lowercase()}.yml with missing entries.")
            currentConfig.save(langFile)
        }
    }

    /**
     * Loads messages from the language file into the configuration.
     *
     * @return A FileConfiguration object containing messages.
     */
    private fun loadMessages(): FileConfiguration {
        val langFile = File(plugin.dataFolder, "lang/messages_${language.lowercase()}.yml")
        return YamlConfiguration.loadConfiguration(langFile)
    }

    /**
     * Reloads messages from the language file.
     */
    fun reloadMessages() {
        messages = loadMessages()
    }

    /**
    * Retrieves the message prefix.
    *
    * @return A string representing the message prefix.
    */
    private fun getPrefix(): String {
        return messages.getString("prefix") ?: "[${plugin.pluginMeta.name}]"
    }

    /**
     * Retrieves a message formatted as an Adventure Component, including the prefix and placeholders.
     *
     * @param category The message category.
     * @param key The message key.
     * @param placeholders A map of placeholders to replace in the message.
     * @return The formatted message as a Component.
     */
    fun getMessage(category: String, key: String, placeholders: Map<String, String> = emptyMap()): Component {
        val prefix = getPrefix()
        val message = messages.getString("$category.$key") ?: run {
            plugin.logger.err("There was an error loading the message $key from category $category")
            "Message not found. Check console..."
        }
        val formattedMessage = placeholders.entries.fold(message) { acc, entry ->
            acc.replace("{${entry.key}}", entry.value)
        }
        val mixMessage =  "$prefix $formattedMessage"
        return formatMixedTextToMiniMessage(mixMessage, TagResolver.empty())
    }

    /**
     * Retrieves a message as a simple string with a prefix and placeholders.
     *
     * @param category The message category.
     * @param key The message key.
     * @param placeholders A map of placeholders.
     * @return The formatted message as a String.
     */
    fun getSimpleMessage(category: String, key: String, placeholders: Map<String, String> = emptyMap()): String {
        val prefix = getPrefix()
        val message = messages.getString("$category.$key") ?: run {
            plugin.logger.err("There was an error loading the message $key from category $category")
            "Message not found. Check console..."
        }
        val formattedMessage = placeholders.entries.fold(message) { acc, entry ->
            acc.replace("{${entry.key}}", entry.value)
        }
        return "$prefix $formattedMessage"
    }

    /**
     * Retrieves a message without a prefix, including placeholders.
     *
     * @param category The message category.
     * @param key The message key.
     * @param placeholders A map of placeholders.
     * @return The message as a String.
     */
    fun getCleanMessage(category: String, key: String, placeholders: Map<String, String> = emptyMap()): String {
        val message = messages.getString("$category.$key") ?: run {
            plugin.logger.err("There was an error loading the message $key from category $category")
            "Message not found. Check console..."
        }
        val formattedMessage = placeholders.entries.fold(message) { acc, entry ->
            acc.replace("{${entry.key}}", entry.value)
        }
        return formattedMessage
    }

    /**
     * Retrieves a log message as an Adventure Component.
     *
     * @param category The message category.
     * @param key The message key.
     * @param placeholders A map of placeholders.
     * @return The formatted message as a Component.
     */
    fun getLogMessage(category: String, key: String, placeholders: Map<String, String> = emptyMap()): Component {
        val message = messages.getString("$category.$key") ?: run {
            plugin.logger.err("There was an error loading the message $key from category $category")
            "Message not found. Check console..."
        }
        val formattedMessage = placeholders.entries.fold(message) { acc, entry ->
            acc.replace("{${entry.key}}", entry.value)
        }
        return formatMixedTextToMiniMessage(formattedMessage, TagResolver.empty())
    }

    /**
     * Retrieves a list of messages formatted as Adventure Components.
     *
     * @param category The message category.
     * @param key The message key.
     * @param placeholders A map of placeholders.
     * @return A list of Components representing formatted messages.
     */
    fun getComplexMessage(category: String, key: String, placeholders: Map<String, String> = emptyMap()): List<Component> {
        val messageList = messages.getStringList("$category.$key")
        if (messageList.isEmpty()) {
            plugin.logger.err("There was an error loading the message list $key from category $category")
            return listOf(Component.text("Message list not found. Check console..."))
        }
        return messageList.map { message ->
            val formattedMessage = placeholders.entries.fold(message) { acc, entry ->
                acc.replace("{${entry.key}}", entry.value)
            }
            formatMixedTextToMiniMessage(formattedMessage, TagResolver.empty())
        }
    }

    /**
     * Retrieves a list of reasons for a given category and key.
     *
     * @param category The message category.
     * @param key The message key.
     * @return A list of reasons as Strings.
     */
    fun getReasons(category: String, key: String): List<String> {
        return messages.getStringList("$category.$key")
    }

    /**
     * Converts Legacy text (&-codes) to an Adventure Component.
     *
     * @param message The message to convert.
     * @return An Adventure Component with proper formatting.
     */
    fun formatLegacyText(message: String): Component {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message)
    }

    /**
     * Converts Legacy text (&-codes) to a properly formatted Bukkit text.
     *
     * @param message The message to convert.
     * @return A properly formatted Bukkit text.
     */
    fun formatLegacyTextBukkit(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }

    /**
     * Converts text containing both Legacy (&) and HEX color codes to an Adventure Component.
     *
     * @param message The message to convert.
     * @return An Adventure Component with proper formatting.
     */
    fun formatHexAndLegacyText(message: String): Component {

        val hexFormatted = message.replace("&#([a-fA-F0-9]{6})".toRegex()) {
            val hex = it.groupValues[1]
            "§x§${hex[0]}§${hex[1]}§${hex[2]}§${hex[3]}§${hex[4]}§${hex[5]}"
        }

        return LegacyComponentSerializer.legacySection().deserialize(hexFormatted)
    }

    /**
     * Formats a message using MiniMessage.
     *
     * @param message The message to format.
     * @return An Adventure Component with proper MiniMessage formatting.
     */
    fun miniMessageFormat(message: String): Component {
        return MiniMessage.miniMessage().deserialize(message)
    }

    /**
     * Retrieves ANSI text from an Adventure Component.
     *
     * @param component The Adventure Component.
     * @return The ANSI formatted text.
     */
    fun getANSIText(component: Component): String {
        return ANSIComponentSerializer.ansi().serialize(component)
    }

    /**
     * Retrieves plain text from an Adventure Component.
     *
     * @param component The Adventure Component.
     * @return The plain text.
     */
    fun getPlainText(component: Component): String {
        return PlainTextComponentSerializer.plainText().serialize(component)
    }

    /**
     * Converts Legacy (&) and HEX color codes to MiniMessage format.
     *
     * @param message The message to convert.
     * @return An Adventure Component with proper MiniMessage formatting.
     */
    private fun convertLegacyToMiniMessage(message: String): String {
        val legacyMap = mapOf(
            "&0" to "<black>",
            "&1" to "<dark_blue>",
            "&2" to "<dark_green>",
            "&3" to "<dark_aqua>",
            "&4" to "<dark_red>",
            "&5" to "<dark_purple>",
            "&6" to "<gold>",
            "&7" to "<gray>",
            "&8" to "<dark_gray>",
            "&9" to "<blue>",
            "&a" to "<green>",
            "&b" to "<aqua>",
            "&c" to "<red>",
            "&d" to "<light_purple>",
            "&e" to "<yellow>",
            "&f" to "<white>",
            "&k" to "<obfuscated>",
            "&l" to "<bold>",
            "&m" to "<strikethrough>",
            "&n" to "<underlined>",
            "&o" to "<italic>",
            "&r" to "<reset>"
        )

        fun processLegacy(segment: String): String {
            return segment.replace(Regex("&([0-9a-fklmnor])")) { matchResult ->
                legacyMap["&${matchResult.groupValues[1]}"] ?: matchResult.value
            }
        }

        val pattern = Regex("(<[^>]+>|\\{[^}]+})")
        val result = StringBuilder()
        var lastIndex = 0

        // Iterujemy po dopasowaniach – dzięki temu zostawiamy niezmienione treści w tagach
        for (match in pattern.findAll(message)) {
            val start = match.range.first
            if (start > lastIndex) {
                val nonTag = message.substring(lastIndex, start)
                result.append(processLegacy(nonTag))
            }
            result.append(match.value)
            lastIndex = match.range.last + 1
        }
        if (lastIndex < message.length) {
            result.append(processLegacy(message.substring(lastIndex)))
        }
        return result.toString()
    }

    /**
     * Converts Bukkit Legacy (§) color codes to MiniMessage format.
     *
     * @param message The message to convert.
     * @return The message in MiniMessage format.
     */
    private fun convertSectionSignToMiniMessage(message: String): String {
        val sectionMap = mapOf(
            "§0" to "<black>",
            "§1" to "<dark_blue>",
            "§2" to "<dark_green>",
            "§3" to "<dark_aqua>",
            "§4" to "<dark_red>",
            "§5" to "<dark_purple>",
            "§6" to "<gold>",
            "§7" to "<gray>",
            "§8" to "<dark_gray>",
            "§9" to "<blue>",
            "§a" to "<green>",
            "§b" to "<aqua>",
            "§c" to "<red>",
            "§d" to "<light_purple>",
            "§e" to "<yellow>",
            "§f" to "<white>",
            "§k" to "<obfuscated>",
            "§l" to "<bold>",
            "§m" to "<strikethrough>",
            "§n" to "<underlined>",
            "§o" to "<italic>",
            "§r" to "<reset>"
        )

        fun processSection(segment: String): String {
            return segment.replace(Regex("§([0-9a-fklmnor])")) { matchResult ->
                sectionMap["§${matchResult.groupValues[1]}"] ?: matchResult.value
            }
        }

        val pattern = Regex("(<[^>]+>|\\{[^}]+})")
        val result = StringBuilder()
        var lastIndex = 0

        for (match in pattern.findAll(message)) {
            val start = match.range.first
            if (start > lastIndex) {
                val nonTag = message.substring(lastIndex, start)
                result.append(processSection(nonTag))
            }
            result.append(match.value)
            lastIndex = match.range.last + 1
        }
        if (lastIndex < message.length) {
            result.append(processSection(message.substring(lastIndex)))
        }
        return result.toString()
    }

    /**
     * Converts HEX color codes (&#RRGGBB) to MiniMessage format.
     *
     * @param message The message to convert.
     * @return The message in MiniMessage format.
     */
    private fun convertHexToMiniMessage(message: String): String {
        return message.replace("&#([a-fA-F0-9]{6})".toRegex()) {
            val hex = it.groupValues[1]
            "<#$hex>"
        }
    }

    /**
     * Converts Unicode escape sequences to their respective characters.
     *
     * @param input The input string.
     * @return The string with Unicode escape sequences converted to characters.
     */
    private fun convertUnicodeEscapeSequences(input: String): String {
        return input.replace(Regex("""\\u([0-9A-Fa-f]{4})""")) { matchResult ->
            val codePoint = matchResult.groupValues[1].toInt(16)
            String(Character.toChars(codePoint))
        }
    }

    /**
     * Formats mixed text containing Legacy, HEX, and Unicode escape sequences to an Adventure Component.
     *
     * @param message The message to format.
     * @param resolver The TagResolver to use.
     * @return An Adventure Component with proper formatting.
     */
    fun formatMixedTextToMiniMessage(message: String, resolver: TagResolver?): Component {
        plugin.logger.debug("Formatting mixed text to MiniMessage: $message")
        var formattedMessage = convertSectionSignToMiniMessage(message)
        plugin.logger.debug("After converting section signs: $formattedMessage")
        formattedMessage = convertLegacyToMiniMessage(formattedMessage)
        plugin.logger.debug("After converting legacy: $formattedMessage")
        formattedMessage = convertHexToMiniMessage(formattedMessage)
        plugin.logger.debug("After converting hex: $formattedMessage")
        formattedMessage = convertUnicodeEscapeSequences(formattedMessage)
        plugin.logger.debug("After converting Unicode escape sequences: $formattedMessage")
        return if (resolver != null) {
            MiniMessage.miniMessage().deserialize(formattedMessage, resolver)
        }else{
            MiniMessage.miniMessage().deserialize(formattedMessage)
        }
    }

}

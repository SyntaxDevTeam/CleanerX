package pl.syntaxdevteam.cleanerx.api

import org.bukkit.plugin.java.JavaPlugin
import pl.syntaxdevteam.cleanerx.base.WordFilter

/**
 * Implementation of the [CleanerXAPI] interface of the CleanerX plugin.
 *
 * This class uses the internal logic defined in the [WordFilter] class,
 * which is responsible for loading the configuration and filtering messages.
 * Thanks to this implementation, the methods [containsBannedWord] and [censorMessage]
 * operate consistently with the CleanerX plugin configuration, using the list of banned
 * and whitelisted words.
 *
 * @constructor Creates an API instance using the provided [plugin] object of the [JavaPlugin] class.
 * During initialization, a [WordFilter] object is created, which automatically loads
 * the banned and whitelisted words configuration from YAML files.
 *
 * @param plugin The main instance of the CleanerX plugin, used to retrieve configuration,
 *               save resources, and log events.
 */
class CleanerXApiImpl(plugin: JavaPlugin) : CleanerXAPI {
    private val wordFilter = WordFilter(plugin)

    /**
     * Checks if the provided message contains banned words.
     *
     * This method delegates the call to [WordFilter.containsBannedWord], which analyzes the message
     * in the context of the loaded banned and whitelisted words.
     *
     * @param message The message to be checked.
     * @return `true` if the message contains banned words, `false` otherwise.
     */
    override fun containsBannedWord(message: String): Boolean {
        return wordFilter.containsBannedWord(message)
    }

    /**
     * Censors the given message by replacing detected banned words with censorship symbols.
     *
     * This method delegates the call to [WordFilter.censorMessage], which processes the message
     * based on the CleanerX plugin configuration. Depending on the [fullCensorship] parameter value,
     * the method performs full or partial censorship of detected words.
     *
     * @param message The message to be censored.
     * @param fullCensorship Defines the censorship mode:
     *        - `true` – full censorship, the entire word is replaced,
     *        - `false` – only part of the word is censored.
     * @return Returns the modified message with banned words replaced by censorship symbols.
     */
    override fun censorMessage(message: String, fullCensorship: Boolean): String {
        return wordFilter.censorMessage(message, fullCensorship)
    }
}

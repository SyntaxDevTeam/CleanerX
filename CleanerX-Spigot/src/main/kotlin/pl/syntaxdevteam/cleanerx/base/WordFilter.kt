package pl.syntaxdevteam.cleanerx.base

import org.bukkit.configuration.file.YamlConfiguration
import pl.syntaxdevteam.cleanerx.CleanerX
import java.io.File
import java.util.*

/**
 * WordFilter is responsible for filtering messages for banned words and censoring them if necessary.
 *
 * @property plugin The instance of the CleanerX plugin.
 * @constructor Creates an instance of WordFilter.
 */
class WordFilter(private val plugin: CleanerX) {

    private var bannedWords: MutableList<String> = mutableListOf()
    private var whitelistWords: MutableList<String> = mutableListOf()

    /**
     * Loads the banned words and whitelist words from the configuration files.
     */
    init {
        loadBannedWords()
        loadWhitelistWords()
    }

    /**
     * Loads the banned words from the 'banned_words.yml' configuration file.
     * If the file does not exist, it creates a default one.
     */
    private fun loadBannedWords() {
        val file = File(plugin.dataFolder, "banned_words.yml")
        if (!file.exists()) {
            plugin.saveResource("banned_words.yml", false)
        }
        val config = YamlConfiguration.loadConfiguration(file)
        bannedWords = config.getStringList("bannedWords").map { it.lowercase(Locale.getDefault()) }.toMutableList()
    }

    /**
     * Loads the whitelist words from the 'whitelist_words.yml' configuration file.
     * If the file does not exist, it creates a default one.
     */
    private fun loadWhitelistWords() {
        val file = File(plugin.dataFolder, "whitelist_words.yml")
        if (!file.exists()) {
            plugin.saveResource("whitelist_words.yml", false)
        }
        val config = YamlConfiguration.loadConfiguration(file)
        whitelistWords = config.getStringList("whitelistWords").map { it.lowercase(Locale.getDefault()) }.toMutableList()
    }

    /**
     * Checks if the given message contains any banned words that are not in the whitelist.
     *
     * @param message The message to check.
     * @return True if the message contains any banned words, false otherwise.
     */
    fun containsBannedWord(message: String): Boolean {
        val lowerMessage = message.lowercase(Locale.getDefault())
        return bannedWords.any { lowerMessage.contains(it) && !whitelistWords.any { word -> lowerMessage.contains(word) } }
    }

    /**
     * Censors the banned words in the given message, ignoring words in the whitelist.
     *
     * @param message The message to censor.
     * @param fullCensorship If true, the entire word will be replaced with asterisks. If false, only part of the word will be censored.
     * @return The censored message.
     */
    fun censorMessage(message: String, fullCensorship: Boolean): String {
        val words = message.split("\\s+".toRegex()).toMutableList()
        for (i in words.indices) {
            for (bannedWord in bannedWords) {
                if (words[i].lowercase(Locale.getDefault()).contains(bannedWord) && !whitelistWords.contains(words[i].lowercase(Locale.getDefault()))) {
                    val replacement = if (fullCensorship) "*".repeat(words[i].length) else words[i].substring(0, 2) + "*".repeat(words[i].length - 2)
                    words[i] = replacement
                }
            }
        }
        return words.joinToString(" ")
    }

    /**
     * Adds a new word to the list of banned words and saves the updated list to the configuration file.
     *
     * @param word The word to add to the banned words list.
     */
    fun addBannedWord(word: String) {
        val lowerWord = word.lowercase(Locale.getDefault())
        if (!bannedWords.contains(lowerWord)) {
            bannedWords.add(lowerWord)
            saveBannedWords()
            plugin.logger.info("Word $word added to banned words list.")
        }
    }

    /**
     * Removes a word from the list of banned words and saves the updated list to the configuration file.
     *
     * @param word The word to remove from the banned words list.
     */
    fun removeBannedWord(word: String): Boolean {
        val lowerWord = word.lowercase(Locale.getDefault())
        if (bannedWords.contains(lowerWord)) {
            bannedWords.remove(lowerWord)
            saveBannedWords()
            plugin.logger.info("Word $word removed from banned words list.")
            return true
        } else {
            plugin.logger.warning("Word $word not found in banned words list.")
            return false
        }
    }

    /**
     * Adds a new word to the whitelist and saves the updated list to the configuration file.
     *
     * @param word The word to add to the whitelist.
     */
    fun addWhitelistWord(word: String) {
        val lowerWord = word.lowercase(Locale.getDefault())
        if (!whitelistWords.contains(lowerWord)) {
            whitelistWords.add(lowerWord)
            saveWhitelistWords()
            plugin.logger.info("Word $word added to whitelist.")
        }
    }

    /**
     * Removes a word from the whitelist and saves the updated list to the configuration file.
     *
     * @param word The word to remove from the whitelist.
     */
    fun removeWhitelistWord(word: String): Boolean {
        val lowerWord = word.lowercase(Locale.getDefault())
        if (whitelistWords.contains(lowerWord)) {
            whitelistWords.remove(lowerWord)
            saveWhitelistWords()
            plugin.logger.info("Word $word removed from whitelist.")
            return true
        } else {
            plugin.logger.warning("Word $word not found in whitelist.")
            return false
        }
    }

    /**
     * Retrieves the current list of banned words.
     *
     * @return The list of banned words.
     */
    fun getBannedWords(): List<String> {
        return bannedWords
    }

    /**
     * Retrieves the current list of whitelist words.
     *
     * @return The list of whitelist words.
     */
    fun getWhitelistWords(): List<String> {
        return whitelistWords
    }

    /**
     * Saves the current list of banned words to the 'banned_words.yml' configuration file.
     */
    private fun saveBannedWords() {
        val file = File(plugin.dataFolder, "banned_words.yml")
        val config = YamlConfiguration()
        config.set("bannedWords", bannedWords)
        config.save(file)
    }

    /**
     * Saves the current list of whitelist words to the 'whitelist_words.yml' configuration file.
     */
    private fun saveWhitelistWords() {
        val file = File(plugin.dataFolder, "whitelist_words.yml")
        val config = YamlConfiguration()
        config.set("whitelistWords", whitelistWords)
        config.save(file)
    }
}
package pl.syntaxdevteam.cleanerx.base

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class WordFilter(private val plugin: JavaPlugin) {

    private var bannedWords: MutableList<String> = mutableListOf()

    init {
        loadBannedWords()
    }

    private fun loadBannedWords() {
        val file = File(plugin.dataFolder, "banned_words.yml")
        if (!file.exists()) {
            plugin.saveResource("banned_words.yml", false)
        }
        val config = YamlConfiguration.loadConfiguration(file)
        bannedWords = config.getStringList("bannedWords").map { it.lowercase(Locale.getDefault()) }.toMutableList()
    }

    fun containsBannedWord(message: String): Boolean {
        val lowerMessage = message.lowercase(Locale.getDefault())
        return bannedWords.any { lowerMessage.contains(it) }
    }

    fun censorMessage(message: String, fullCensorship: Boolean): String {
        val words = message.split("\\s+".toRegex()).toMutableList()
        for (i in words.indices) {
            for (bannedWord in bannedWords) {
                if (words[i].lowercase(Locale.getDefault()).contains(bannedWord)) {
                    val replacement = if (fullCensorship) "*".repeat(words[i].length) else words[i].substring(0, 2) + "*".repeat(words[i].length - 2)
                    words[i] = replacement
                }
            }
        }
        return words.joinToString(" ")
    }

    fun addBannedWord(word: String) {
        val lowerWord = word.lowercase(Locale.getDefault())
        if (!bannedWords.contains(lowerWord)) {
            bannedWords.add(lowerWord)
            saveBannedWords()
        }
    }

    private fun saveBannedWords() {
        val file = File(plugin.dataFolder, "banned_words.yml")
        val config = YamlConfiguration()
        config.set("bannedWords", bannedWords)
        config.save(file)
    }
}

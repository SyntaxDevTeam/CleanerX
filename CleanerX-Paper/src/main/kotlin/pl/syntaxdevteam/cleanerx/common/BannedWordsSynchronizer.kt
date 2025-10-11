package pl.syntaxdevteam.cleanerx.common

import org.bukkit.configuration.file.YamlConfiguration
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.cleanerx.util.SchedulerAdapter
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * Synchronizes the banned words configuration between the plugin resources and the server file.
 *
 * This class loads the banned words defined in the plugin resources and ensures that every word
 * exists in the server's configuration file. Missing words are appended to the file without
 * removing or duplicating the existing ones. All heavy IO operations are executed asynchronously
 * to avoid blocking the main server thread.
 */
class BannedWordsSynchronizer(private val plugin: CleanerX) {

    /**
     * Synchronizes the banned words file with the plugin resources. Once the synchronization is
     * completed, the provided [onComplete] callback is executed on the main thread with the final
     * list of banned words present in the server file.
     */
    fun synchronize(onComplete: ((List<String>) -> Unit)? = null) {
        SchedulerAdapter.runAsync(plugin) {
            try {
                val serverFile = File(plugin.dataFolder, BANNED_WORDS_FILE)
                if (!serverFile.exists()) {
                    plugin.saveResource(BANNED_WORDS_FILE, false)
                }

                val serverConfig = YamlConfiguration.loadConfiguration(serverFile)
                val serverWords = serverConfig.getStringList(BANNED_WORDS_KEY).toMutableList()
                val resourceWords = loadResourceWords()

                if (resourceWords.isEmpty()) {
                    plugin.logger.warning("The resource banned_words.yml file is empty or missing the bannedWords section.")
                    complete(onComplete, serverWords.toList())
                    return@runAsync
                }

                val normalizedExisting = serverWords
                    .map { it.lowercase(Locale.ROOT) }
                    .toMutableSet()

                var modified = false
                for (word in resourceWords) {
                    val normalized = word.lowercase(Locale.ROOT)
                    if (!normalizedExisting.contains(normalized)) {
                        serverWords.add(word)
                        normalizedExisting.add(normalized)
                        modified = true
                    }
                }

                if (modified) {
                    serverConfig.set(BANNED_WORDS_KEY, serverWords)
                    serverConfig.save(serverFile)
                    plugin.logger.info("Synchronized banned_words.yml with plugin resources.")
                }

                complete(onComplete, serverWords.toList())
            } catch (exception: Exception) {
                plugin.logger.err("Failed to synchronize banned words: ${exception.message}")
            }
        }
    }

    private fun loadResourceWords(): List<String> {
        val resourceStream = plugin.getResource(BANNED_WORDS_FILE)
        if (resourceStream == null) {
            plugin.logger.err("Unable to locate $BANNED_WORDS_FILE inside plugin resources.")
            return emptyList()
        }

        resourceStream.use { stream ->
            InputStreamReader(stream, StandardCharsets.UTF_8).use { reader ->
                val config = YamlConfiguration.loadConfiguration(reader)
                return config.getStringList(BANNED_WORDS_KEY)
            }
        }
    }

    private fun complete(onComplete: ((List<String>) -> Unit)?, result: List<String>) {
        if (onComplete == null) {
            return
        }
        SchedulerAdapter.runSync(plugin) {
            onComplete(result)
        }
    }

    private companion object {
        private const val BANNED_WORDS_FILE = "banned_words.yml"
        private const val BANNED_WORDS_KEY = "bannedWords"
    }
}
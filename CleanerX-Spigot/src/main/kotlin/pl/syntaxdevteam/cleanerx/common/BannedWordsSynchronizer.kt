package pl.syntaxdevteam.cleanerx.common

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import pl.syntaxdevteam.cleanerx.CleanerX
import java.io.File
import java.io.InputStreamReader
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val serverFile = File(plugin.dataFolder, BANNED_WORDS_FILE)
                if (!serverFile.exists()) {
                    plugin.saveResource(BANNED_WORDS_FILE, false)
                }

                val serverConfig = YamlConfiguration.loadConfiguration(serverFile)
                val serverWords = serverConfig.getStringList(BANNED_WORDS_KEY).toMutableList()
                val syncEnabled = plugin.config.getBoolean(REMOTE_SYNC_ENABLED_KEY, false)
                val includeResourceWords = plugin.config.getBoolean(INCLUDE_RESOURCE_WORDS_KEY, true)
                val timeoutMillis = plugin.config.getLong(REMOTE_SYNC_TIMEOUT_KEY, DEFAULT_REMOTE_TIMEOUT_MS).coerceAtLeast(500L)
                val remoteSources = plugin.config.getStringList(REMOTE_SYNC_SOURCES_KEY).filter { it.isNotBlank() }

                val mergedWords = serverWords.toMutableList()
                val normalizedExisting = mergedWords
                    .map { it.lowercase(Locale.ROOT) }
                    .toMutableSet()

                if (includeResourceWords) {
                    mergeWords(mergedWords, normalizedExisting, loadResourceWords())
                }

                if (syncEnabled && remoteSources.isNotEmpty()) {
                    val remoteWords = loadRemoteWords(remoteSources, timeoutMillis)
                    mergeWords(mergedWords, normalizedExisting, remoteWords)
                }

                val modified = mergedWords.size != serverWords.size

                if (modified) {
                    serverConfig.set(BANNED_WORDS_KEY, mergedWords)
                    serverConfig.save(serverFile)
                    plugin.logger.info("Synchronized banned_words.yml with configured sources.")
                }

                complete(onComplete, mergedWords.toList())
            } catch (exception: Exception) {
                plugin.logger.err("Failed to synchronize banned words: ${exception.message}")
            }
        })
    }

    private fun mergeWords(target: MutableList<String>, normalized: MutableSet<String>, words: List<String>) {
        for (word in words) {
            val normalizedWord = word.lowercase(Locale.ROOT)
            if (!normalized.contains(normalizedWord)) {
                target.add(word)
                normalized.add(normalizedWord)
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
                return sanitizeWords(config.getStringList(BANNED_WORDS_KEY))
            }
        }
    }

    private fun loadRemoteWords(sources: List<String>, timeoutMillis: Long): List<String> {
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(timeoutMillis))
            .build()

        val words = mutableListOf<String>()
        for (source in sources) {
            runCatching {
                val request = HttpRequest.newBuilder(URI.create(source))
                    .timeout(Duration.ofMillis(timeoutMillis))
                    .header("Accept", "text/plain, text/yaml, application/yaml, application/json")
                    .GET()
                    .build()

                val response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                if (response.statusCode() in 200..299) {
                    words.addAll(extractWordsFromPayload(response.body()))
                } else {
                    plugin.logger.warning("Banned words sync skipped source $source due to HTTP ${response.statusCode()}.")
                }
            }.onFailure { exception ->
                plugin.logger.warning("Banned words sync failed for $source: ${exception.message}")
            }
        }

        return sanitizeWords(words)
    }

    private fun extractWordsFromPayload(payload: String): List<String> {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) {
            return emptyList()
        }

        val fromYaml = if (looksLikeYamlWithBannedWords(trimmed)) {
            runCatching {
                val config = YamlConfiguration.loadConfiguration(StringReader(trimmed))
                config.getStringList(BANNED_WORDS_KEY)
            }.getOrNull().orEmpty()
        } else {
            emptyList()
        }

        if (fromYaml.isNotEmpty()) {
            return fromYaml
        }

        return trimmed.lineSequence()
            .map { line ->
                line.substringBefore("#")
                    .substringBefore("//")
                    .trim()
            }
            .filter { it.isNotEmpty() }
            .toList()
    }

    private fun looksLikeYamlWithBannedWords(payload: String): Boolean {
        return payload.lineSequence()
            .map { it.trimStart() }
            .any { it.startsWith("$BANNED_WORDS_KEY:") }
    }

    private fun sanitizeWords(words: List<String>): List<String> {
        return words.asSequence()
            .map { it.trim().lowercase(Locale.ROOT) }
            .filter { it.length >= MIN_WORD_LENGTH }
            .filter { CANDIDATE_WORD_REGEX.matches(it) }
            .distinct()
            .toList()
    }

    private fun complete(onComplete: ((List<String>) -> Unit)?, result: List<String>) {
        if (onComplete == null) {
            return
        }
        Bukkit.getScheduler().runTask(plugin, Runnable {
            onComplete(result)
        })
    }

    private companion object {
        private const val BANNED_WORDS_FILE = "banned_words.yml"
        private const val BANNED_WORDS_KEY = "bannedWords"
        private const val REMOTE_SYNC_ENABLED_KEY = "banned-words-sync.enabled"
        private const val INCLUDE_RESOURCE_WORDS_KEY = "banned-words-sync.include-default-resource"
        private const val REMOTE_SYNC_TIMEOUT_KEY = "banned-words-sync.timeout-ms"
        private const val REMOTE_SYNC_SOURCES_KEY = "banned-words-sync.remote-sources"
        private const val DEFAULT_REMOTE_TIMEOUT_MS = 2500L
        private const val MIN_WORD_LENGTH = 3
        private val CANDIDATE_WORD_REGEX = Regex("^[\\p{L}\\p{Nd}@!$+|]+$")
    }
}

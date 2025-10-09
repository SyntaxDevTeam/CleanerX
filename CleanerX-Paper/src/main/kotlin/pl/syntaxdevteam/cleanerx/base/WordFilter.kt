package pl.syntaxdevteam.cleanerx.base

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.Locale

/**
 * WordFilter is responsible for filtering messages for banned words and censoring them if necessary.
 *
 * @property plugin The instance of the CleanerX plugin.
 * @constructor Creates an instance of WordFilter.
 */
class WordFilter(private val plugin: JavaPlugin) {

    private var bannedWords: MutableList<String> = mutableListOf()
    private var whitelistWords: MutableList<String> = mutableListOf()
    private var bannedPatterns: List<FilterPattern> = emptyList()
    private var whitelistPatterns: List<FilterPattern> = emptyList()

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
        updateBannedWords(config.getStringList("bannedWords"))
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
        whitelistWords = config.getStringList("whitelistWords")
            .map { it.lowercase(Locale.ROOT) }
            .toMutableList()
        whitelistPatterns = whitelistWords.mapNotNull { toFilterPattern(it) }
    }

    /**
     * Checks if the given message contains any banned words that are not in the whitelist.
     *
     * @param message The message to check.
     * @return True if the message contains any banned words, false otherwise.
     */
    fun containsBannedWord(message: String): Boolean {
        if (message.isBlank() || bannedPatterns.isEmpty()) {
            return false
        }

        val normalizedMessage = buildNormalizedMessage(message)
        val whitelistedRanges = collectWhitelistRanges(normalizedMessage)

        for (pattern in bannedPatterns) {
            val matches = pattern.pattern.findAll(normalizedMessage.normalized)
            for (match in matches) {
                if (!isRangeWhitelisted(match.range, whitelistedRanges)) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Censors the banned words in the given message, ignoring words in the whitelist.
     *
     * @param message The message to censor.
     * @param fullCensorship If true, the entire word will be replaced with asterisks. If false, only part of the word will be censored.
     * @return The censored message.
     */
    fun censorMessage(message: String, fullCensorship: Boolean): String {
        if (message.isEmpty() || bannedPatterns.isEmpty()) {
            return message
        }

        val normalizedMessage = buildNormalizedMessage(message)
        val whitelistedRanges = collectWhitelistRanges(normalizedMessage)
        val chars = message.toCharArray()

        for (pattern in bannedPatterns) {
            val matches = pattern.pattern.findAll(normalizedMessage.normalized)
            for (match in matches) {
                if (isRangeWhitelisted(match.range, whitelistedRanges)) {
                    continue
                }

                val indices = match.range
                    .flatMap { normalizedIndex ->
                        val originalIndex = normalizedMessage.indexMap.getOrNull(normalizedIndex)
                        if (originalIndex != null) listOf(originalIndex) else emptyList()
                    }
                    .toSortedSet()

                if (indices.isEmpty()) {
                    continue
                }

                if (fullCensorship) {
                    indices.forEach { index ->
                        if (index in chars.indices && !chars[index].isWhitespace()) {
                            chars[index] = '*'
                        }
                    }
                } else {
                    var remainingVisible = 2
                    indices.forEach { index ->
                        if (index !in chars.indices || chars[index].isWhitespace()) {
                            return@forEach
                        }
                        if (remainingVisible > 0) {
                            remainingVisible--
                        } else {
                            chars[index] = '*'
                        }
                    }
                }
            }
        }

        return String(chars)
    }

    /**
     * Adds a new word to the list of banned words and saves the updated list to the configuration file.
     *
     * @param word The word to add to the banned words list.
     */
    fun addBannedWord(word: String) {
        val lowerWord = word.lowercase(Locale.ROOT)
        if (!bannedWords.contains(lowerWord)) {
            bannedWords.add(lowerWord)
            bannedPatterns = bannedWords.mapNotNull { toFilterPattern(it) }
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
        val lowerWord = word.lowercase(Locale.ROOT)
        return if (bannedWords.contains(lowerWord)) {
            bannedWords.remove(lowerWord)
            saveBannedWords()
            plugin.logger.info("Word $word removed from banned words list.")
            true
        } else {
            plugin.logger.warning("Word $word not found in banned words list.")
            false
        }
    }

    /**
     * Adds a new word to the whitelist and saves the updated list to the configuration file.
     *
     * @param word The word to add to the whitelist.
     */
    fun addWhitelistWord(word: String) {
        val lowerWord = word.lowercase(Locale.ROOT)
        if (!whitelistWords.contains(lowerWord)) {
            whitelistWords.add(lowerWord)
            whitelistPatterns = whitelistWords.mapNotNull { toFilterPattern(it) }
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
        val lowerWord = word.lowercase(Locale.ROOT)
        return if (whitelistWords.contains(lowerWord)) {
            whitelistWords.remove(lowerWord)
            saveWhitelistWords()
            plugin.logger.info("Word $word removed from whitelist.")
            true
        } else {
            plugin.logger.warning("Word $word not found in whitelist.")
            false
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
     * Updates the banned words in memory using the provided list.
     *
     * @param words The words that should be considered banned.
     */
    fun updateBannedWords(words: List<String>) {
        bannedWords = words
            .map { it.lowercase(Locale.ROOT) }
            .toMutableList()
        bannedPatterns = bannedWords.mapNotNull { toFilterPattern(it) }
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


    private fun toFilterPattern(word: String): FilterPattern? {
        val normalized = normalizeWord(word)
        if (normalized.isEmpty()) {
            return null
        }

        return FilterPattern(word, normalized, buildFuzzyRegex(normalized))
    }

    private fun buildFuzzyRegex(normalizedWord: String): Regex {
        val builder = StringBuilder()
        builder.append("(?<![a-z])")
        normalizedWord.forEachIndexed { index, character ->
            builder.append(Regex.escape(character.toString()))
            if (index < normalizedWord.lastIndex) {
                builder.append("[^a-z]*")
            }
        }
        builder.append("(?![a-z])")
        return Regex(builder.toString())
    }

    private fun normalizeWord(word: String): String {
        if (word.isBlank()) {
            return ""
        }

        val normalizedBuilder = StringBuilder()
        word.lowercase(Locale.ROOT).forEach { character ->
            val replacement = CHARACTER_REPLACEMENTS[character] ?: character.toString()
            replacement.forEach { replacementChar ->
                val normalized = stripDiacritics(replacementChar.lowercaseChar().toString())
                normalized.forEach { normalizedChar ->
                    if (normalizedChar in 'a'..'z') {
                        normalizedBuilder.append(normalizedChar)
                    }
                }
            }
        }

        return normalizedBuilder.toString()
    }

    private fun buildNormalizedMessage(message: String): NormalizedMessage {
        val normalizedBuilder = StringBuilder()
        val indexMap = mutableListOf<Int>()

        message.forEachIndexed { index, rawChar ->
            val lowerChar = rawChar.lowercaseChar()
            val replacement = CHARACTER_REPLACEMENTS[lowerChar] ?: lowerChar.toString()
            replacement.forEach { replacementChar ->
                val normalized = stripDiacritics(replacementChar.lowercaseChar().toString())
                if (normalized.isEmpty()) {
                    return@forEach
                }
                normalized.forEach { normalizedChar ->
                    val finalChar = when {
                        normalizedChar in 'A'..'Z' -> normalizedChar.lowercaseChar()
                        else -> normalizedChar
                    }
                    normalizedBuilder.append(finalChar)
                    indexMap.add(index)
                }
            }
        }

        return NormalizedMessage(normalizedBuilder.toString(), indexMap.toIntArray())
    }

    private fun collectWhitelistRanges(normalizedMessage: NormalizedMessage): List<IntRange> {
        if (whitelistPatterns.isEmpty() || normalizedMessage.normalized.isEmpty()) {
            return emptyList()
        }

        val ranges = mutableListOf<IntRange>()
        for (pattern in whitelistPatterns) {
            val matches = pattern.pattern.findAll(normalizedMessage.normalized)
            matches.forEach { ranges.add(it.range) }
        }

        if (ranges.isEmpty()) {
            return emptyList()
        }

        ranges.sortBy { it.first }
        val mergedRanges = mutableListOf<IntRange>()
        var current = ranges.first()
        for (range in ranges.drop(1)) {
            current = if (range.first <= current.last + 1) {
                current.first..maxOf(current.last, range.last)
            } else {
                mergedRanges.add(current)
                range
            }
        }
        mergedRanges.add(current)

        return mergedRanges
    }

    private fun isRangeWhitelisted(range: IntRange, whitelistedRanges: List<IntRange>): Boolean {
        if (whitelistedRanges.isEmpty()) {
            return false
        }

        return whitelistedRanges.any { whitelistRange ->
            range.first >= whitelistRange.first && range.last <= whitelistRange.last
        }
    }

    private fun stripDiacritics(value: String): String {
        return NON_SPACING_MARKS_REGEX.replace(java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD), "")
    }

    private data class FilterPattern(
        val original: String,
        val normalized: String,
        val pattern: Regex,
    )

    private data class NormalizedMessage(
        val normalized: String,
        val indexMap: IntArray,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NormalizedMessage

            if (normalized != other.normalized) return false
            if (!indexMap.contentEquals(other.indexMap)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = normalized.hashCode()
            result = 31 * result + indexMap.contentHashCode()
            return result
        }
    }

    companion object {
        private val NON_SPACING_MARKS_REGEX = "\\p{Mn}+".toRegex()
        private val CHARACTER_REPLACEMENTS: Map<Char, String> = mapOf(
            '4' to "a",
            '@' to "a",
            'á' to "a",
            'à' to "a",
            'ä' to "a",
            'â' to "a",
            'å' to "a",
            'æ' to "ae",
            'ß' to "ss",
            '3' to "e",
            '€' to "e",
            '1' to "i",
            '!' to "i",
            '|' to "i",
            '0' to "o",
            'ö' to "o",
            'ô' to "o",
            'ò' to "o",
            'ó' to "o",
            'ø' to "o",
            '5' to "s",
            '$' to "s",
            'š' to "s",
            '§' to "s",
            '7' to "t",
            '+' to "t",
            '2' to "z",
            'ž' to "z",
        )
    }
}

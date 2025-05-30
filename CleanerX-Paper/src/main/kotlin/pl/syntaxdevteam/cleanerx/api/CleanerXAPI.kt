package pl.syntaxdevteam.cleanerx.api

/**
 * API interface of the CleanerX plugin.
 *
 * This API allows other plugins to interact with the message filtering mechanisms
 * provided by CleanerX, including checking whether a message contains banned words
 * and censoring messages by replacing detected words with censorship symbols.
 *
 * Example usage:
 * ```
 * // Retrieve the CleanerX API instance via Bukkit Services Manager
 * val cleanerXApi = Bukkit.getServicesManager().load(CleanerXAPI::class.java)
 * if (cleanerXApi != null) {
 *     val message = "Example message"
 *     if (cleanerXApi.containsBannedWord(message)) {
 *         val censoredMessage = cleanerXApi.censorMessage(message)
 *         // further message processing
 *     }
 * }
 * ```
 *
 * The implementation of this interface guarantees consistent filtering operations
 * according to the CleanerX plugin configuration, which defines banned and whitelisted words.
 */
interface CleanerXAPI {

    /**
     * Checks if the given message contains words considered as banned.
     *
     * The method analyzes the provided message by comparing its content with the list of words
     * defined as banned in the CleanerX configuration. If the message contains any banned words
     * and does not contain expressions from the whitelist, the method returns `true`.
     *
     * @param message The message to be checked for banned words.
     * @return `true` if the message contains banned words, otherwise `false`.
     */
    fun containsBannedWord(message: String): Boolean

    /**
     * Censors the message by replacing detected banned words with censorship symbols.
     *
     * The method processes the message content, searching for words from the banned list.
     * If detected, censorship is applied in one of two modes:
     * - **Full censorship:** The entire word is replaced with a string of asterisks (`*`),
     *   where the number of asterisks corresponds to the original word length.
     * - **Partial censorship:** Only part of the word, usually after the first two characters,
     *   is replaced with censorship symbols, preserving some context.
     *
     * @param message The message to be censored.
     * @param fullCensorship Defines the censorship mode:
     *        - `true` – full censorship, the entire word is replaced,
     *        - `false` – only part of the word is censored.
     *        Defaults to `true`.
     * @return Returns the message with detected banned words replaced by censorship symbols.
     */
    fun censorMessage(message: String, fullCensorship: Boolean = true): String
}

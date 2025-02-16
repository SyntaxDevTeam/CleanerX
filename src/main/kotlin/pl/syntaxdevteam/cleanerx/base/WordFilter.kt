package pl.syntaxdevteam.cleanerx.base

    import org.bukkit.configuration.file.YamlConfiguration
    import org.bukkit.plugin.java.JavaPlugin
    import java.io.File
    import java.util.*

    /**
     * WordFilter is responsible for filtering messages for banned words and censoring them if necessary.
     *
     * @property plugin The instance of the CleanerX plugin.
     * @constructor Creates an instance of WordFilter.
     */
    class WordFilter(private val plugin: JavaPlugin) {

        private var bannedWords: MutableList<String> = mutableListOf()

        /**
         * Loads the banned words from the configuration file.
         */
        init {
            loadBannedWords()
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
         * Checks if the given message contains any banned words.
         *
         * @param message The message to check.
         * @return True if the message contains any banned words, false otherwise.
         */
        fun containsBannedWord(message: String): Boolean {
            val lowerMessage = message.lowercase(Locale.getDefault())
            return bannedWords.any { lowerMessage.contains(it) }
        }

        /**
         * Censors the banned words in the given message.
         *
         * @param message The message to censor.
         * @param fullCensorship If true, the entire word will be replaced with asterisks. If false, only part of the word will be censored.
         * @return The censored message.
         */
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
            }
        }

        /**
         * Removes a word from the list of banned words and saves the updated list to the configuration file.
         *
         * @param word The word to remove from the banned words list.
         */
        fun removeBannedWord(word: String) {
            val lowerWord = word.lowercase(Locale.getDefault())
            if (bannedWords.contains(lowerWord)) {
                bannedWords.remove(lowerWord)
                saveBannedWords()
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
         * Saves the current list of banned words to the 'banned_words.yml' configuration file.
         */
        private fun saveBannedWords() {
            val file = File(plugin.dataFolder, "banned_words.yml")
            val config = YamlConfiguration()
            config.set("bannedWords", bannedWords)
            config.save(file)
        }
    }
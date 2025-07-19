package pl.syntaxdevteam.cleanerx.eventhandler

class RegexUrlDetector : UrlDetector {
    companion object {
        private val URL_PATTERN =
            "\\b(?:(?:https?://)|(?:www\\.)|(?:[A-Za-z0-9\\-]+\\.[A-Za-z]{2,}))[^\\s<>]*\\b"
                .toRegex(RegexOption.IGNORE_CASE)
    }
    override fun containsUrl(text: String): Boolean =
        URL_PATTERN.containsMatchIn(text)
}
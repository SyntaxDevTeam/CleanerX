package pl.syntaxdevteam.cleanerx.eventhandler

interface UrlDetector {
    fun containsUrl(text: String): Boolean
}
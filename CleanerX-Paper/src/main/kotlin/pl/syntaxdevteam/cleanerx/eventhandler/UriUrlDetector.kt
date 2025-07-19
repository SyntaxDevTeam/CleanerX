package pl.syntaxdevteam.cleanerx.eventhandler

import java.net.URI

class UriUrlDetector : UrlDetector {
    override fun containsUrl(text: String): Boolean {
        return text.split("\\s+".toRegex()).any { token ->
            try {
                val candidate = if (!token.contains("://")) "http://$token" else token
                val uri = URI(candidate)
                uri.host?.contains('.') == true
            } catch (e: Exception) {
                false
            }
        }
    }
}
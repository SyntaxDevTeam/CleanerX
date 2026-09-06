package pl.syntaxdevteam.cleanerx.eventhandler

import com.google.common.net.InetAddresses
import com.google.common.net.InternetDomainName
import java.net.IDN
import java.net.URI

class UriUrlDetector : UrlDetector {
    override fun containsUrl(text: String): Boolean =
        CANDIDATE.findAll(text).any { match -> isUrl(match.value) }

    private fun isUrl(token: String): Boolean {
        val candidate = token.trimEnd('.', ',', '!', '?', ';', ':', ')', '}', '"', '\'')
        return try {
            val explicitScheme = candidate.startsWith("http://", true) ||
                candidate.startsWith("https://", true)
            val uri = URI(if (explicitScheme) candidate else "http://$candidate")
            val authority = uri.rawAuthority ?: return false
            val host = if (authority.substringAfterLast('@').startsWith("[")) {
                uri.host?.removeSurrounding("[", "]") ?: return false
            } else {
                IDN.toASCII(authority.substringAfterLast('@').substringBefore(':'))
            }
            if (uri.port < -1 || uri.port > 65535) return false
            if (InetAddresses.isInetAddress(host)) return true
            // A sentence dot is not a domain suffix. No DNS lookups are needed.
            InternetDomainName.from(host).isUnderRegistrySuffix
        } catch (_: IllegalArgumentException) {
            false
        } catch (_: java.net.URISyntaxException) {
            false
        }
    }

    companion object {
        private val CANDIDATE = Regex(
            """(?<![\p{L}\p{N}_.-])(?:https?://[^\s<>]+|[\p{L}\p{N}](?:[\p{L}\p{N}-]*[\p{L}\p{N}])?(?:\.[\p{L}\p{N}](?:[\p{L}\p{N}-]*[\p{L}\p{N}])?)+(?![\p{L}\p{N}_-])(?::[0-9]+)?(?:[/\?#][^\s<>]*)?)""",
            RegexOption.IGNORE_CASE
        )
    }
}

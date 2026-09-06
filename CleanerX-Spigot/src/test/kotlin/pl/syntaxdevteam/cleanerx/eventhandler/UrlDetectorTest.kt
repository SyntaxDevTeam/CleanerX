package pl.syntaxdevteam.cleanerx.eventhandler

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlDetectorTest {
    private val detectors = listOf(UriUrlDetector(), RegexUrlDetector())

    @Test
    fun `ordinary punctuation and numbers are not links`() {
        val messages = listOf(
            "ok.", "test.", "np.", ".", "...", "Cześć.", "To jest zdanie.",
            "Tak.Nie", "mam np. kilka rzeczy.", "wersja 1.21.11", "3.14", "12.30",
            "plik.txt", "config.yml", "foo.invalid", "https://", "999.999.999.999"
        )
        detectors.forEach { detector ->
            messages.forEach { assertFalse(detector.containsUrl(it), "${detector.javaClass.simpleName}: $it") }
        }
    }

    @Test
    fun `domains and IP addresses are blocked including surrounding punctuation`() {
        val messages = listOf(
            "example.com", "EXAMPLE.COM", "example.com.", "(example.pl)",
            "wejdź na https://example.com/path?q=1.", "www.example.org",
            "mc.example.co.uk:25565", "discord.gg/test", "example.dev",
            "zażółć.pl", "https://zażółć.pl/test", "127.0.0.1", "127.0.0.1:25565",
            "https://[2001:db8::1]/test", "https://example.com:443/path"
        )
        detectors.forEach { detector ->
            messages.forEach { assertTrue(detector.containsUrl(it), "${detector.javaClass.simpleName}: $it") }
        }
    }
}

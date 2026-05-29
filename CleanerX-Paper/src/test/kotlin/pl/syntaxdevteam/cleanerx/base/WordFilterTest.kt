package pl.syntaxdevteam.cleanerx.base

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.core.logging.Logger
import java.nio.file.Path

class WordFilterTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var wordFilter: WordFilter

    @BeforeEach
    fun setUp() {
        val dataDir = tempDir.toFile()
        dataDir.resolve("banned_words.yml").writeText(
            """
            bannedWords:
              - kurwa
              - idiot
            """.trimIndent()
        )
        dataDir.resolve("whitelist_words.yml").writeText(
            """
            whitelistWords:
              - kurwawosc
            """.trimIndent()
        )

        val plugin = mockk<CleanerX>(relaxed = true)
        every { plugin.dataFolder } returns dataDir
        every { plugin.logger } returns mockk<Logger>(relaxed = true)

        wordFilter = WordFilter(plugin)
    }

    @Test
    fun `wykrywa tokenizacje oraz leetspeak`() {
        assertTrue(wordFilter.containsBannedWord("to jest k.u.r.w.a"))
        assertTrue(wordFilter.containsBannedWord("ale z ciebie 1d10t"))
        assertFalse(wordFilter.containsBannedWord("bez przeklenstw"))
    }

    @Test
    fun `whitelist ignoruje slowa dozwolone nawet gdy zawieraja banned token`() {
        assertFalse(wordFilter.containsBannedWord("To tylko kurwawosc."))
        assertTrue(wordFilter.containsBannedWord("To jednak kurwa."))
    }

    @Test
    fun `cenzura jest case insensitive i wspiera full oraz partial`() {
        assertTrue(wordFilter.containsBannedWord("KURWA"))

        val fullyCensored = wordFilter.censorMessage("To KURWA test", fullCensorship = true)
        assertEquals("To ***** test", fullyCensored)

        val partiallyCensored = wordFilter.censorMessage("To KURWA test", fullCensorship = false)
        assertEquals("To KU*** test", partiallyCensored)
    }


    @Test
    fun `wykrywa i cenzuruje pelne wyrazenia z wieloma slowami`() {
        wordFilter.updateBannedWords(listOf("kurwa mać"))

        assertTrue(wordFilter.containsBannedWord("No i kurwa mać, znowu."))
        assertTrue(wordFilter.containsBannedWord("No i k.u.r.w.a m.a.ć, znowu."))
        assertFalse(wordFilter.containsBannedWord("kurwa, ale bez drugiego slowa"))

        val fullyCensored = wordFilter.censorMessage("No i kurwa mać, znowu.", fullCensorship = true)
        assertEquals("No i ***** ***, znowu.", fullyCensored)
    }

    @Test
    fun `whitelist obsluguje pelne wyrazenia`() {
        wordFilter.addWhitelistWord("kurwa mać")

        assertFalse(wordFilter.containsBannedWord("cytat: kurwa mać."))
        assertTrue(wordFilter.containsBannedWord("pojedyncza kurwa nadal jest blokowana"))
    }

    @Test
    fun `wykrywa odmiane slowa ale nie lapie fragmentu w srodku innego wyrazu`() {
        wordFilter.updateBannedWords(listOf("chuje"))

        assertTrue(wordFilter.containsBannedWord("nie badz chujem"))
        assertFalse(wordFilter.containsBannedWord("wymachuje rekami"))
    }
}

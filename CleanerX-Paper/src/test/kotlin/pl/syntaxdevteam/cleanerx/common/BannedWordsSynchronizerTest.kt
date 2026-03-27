package pl.syntaxdevteam.cleanerx.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BannedWordsSynchronizerTest {

    @Test
    fun `parsuje yaml bannedWords i filtruje niepoprawne wpisy`() {
        val payload = """
            bannedWords:
              - Kurwa
              - chuj
              - xx
              - test!!
              - kurwa
        """.trimIndent()

        val words = BannedWordsSynchronizer.parsePayloadToWords(payload)

        assertEquals(listOf("kurwa", "chuj", "test!!"), words)
    }

    @Test
    fun `parsuje plain text z komentarzami`() {
        val payload = """
            # english
            fuck
            shit // inline comment
            
            dwa
            @@@
        """.trimIndent()

        val words = BannedWordsSynchronizer.parsePayloadToWords(payload)

        assertEquals(listOf("fuck", "shit", "dwa", "@@@"), words)
    }
}

package pl.syntaxdevteam.cleanerx.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FlectonePulseFormattingTest {

    @Test
    fun `nie podmienia szablonu czatu gdy cenzurowany jest tylko userMessage`() {
        val rewrite = rewriteFlectonePulseFormatting(
            message = "<prefix><player>: <message>",
            userMessage = "brzydkie slowo",
            isPlayerMessage = false,
            fullCensorship = true
        ) { text, _ ->
            text.replace("brzydkie slowo", "*************")
        }

        assertTrue(rewrite.changed)
        assertEquals("<prefix><player>: <message>", rewrite.message)
        assertEquals("*************", rewrite.userMessage)
    }

    @Test
    fun `cenzuruje tresc wiadomosci gdy pipeline obrabia player message`() {
        val rewrite = rewriteFlectonePulseFormatting(
            message = "to jest brzydkie slowo",
            userMessage = "",
            isPlayerMessage = true,
            fullCensorship = true
        ) { text, _ ->
            text.replace("brzydkie slowo", "*************")
        }

        assertTrue(rewrite.changed)
        assertEquals("to jest *************", rewrite.message)
        assertEquals("", rewrite.userMessage)
    }

    @Test
    fun `nie zgłasza zmian gdy nic nie zostało ocenzurowane`() {
        val rewrite = rewriteFlectonePulseFormatting(
            message = "czysta wiadomosc",
            userMessage = "",
            isPlayerMessage = true,
            fullCensorship = true
        ) { text, _ -> text }

        assertFalse(rewrite.changed)
        assertEquals("czysta wiadomosc", rewrite.message)
        assertEquals("", rewrite.userMessage)
    }
}

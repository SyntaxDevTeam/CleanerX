package pl.syntaxdevteam.cleanerx.commands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CommandArgumentsTest {

    @Test
    fun `scala argumenty w pelne wyrazenie i usuwa cudzyslowy`() {
        val expression = parseExpressionArgument(arrayOf("add", "\"kurwa", "mać\""))

        assertEquals("kurwa mać", expression)
    }

    @Test
    fun `obsluguje niecytowane wielowyrazowe wyrazenia`() {
        val expression = parseExpressionArgument(arrayOf("remove", "kurwa", "mać"))

        assertEquals("kurwa mać", expression)
    }

    @Test
    fun `zwraca pusty tekst gdy brakuje argumentu`() {
        assertTrue(parseExpressionArgument(arrayOf("add")).isEmpty())
    }
}

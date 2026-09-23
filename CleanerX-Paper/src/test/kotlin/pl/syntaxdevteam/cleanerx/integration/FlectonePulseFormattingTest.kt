package pl.syntaxdevteam.cleanerx.integration

import io.mockk.every
import io.mockk.mockk
import net.flectone.pulse.constant.MessageFlag
import net.flectone.pulse.model.event.message.MessageFormattingEvent
import net.flectone.pulse.model.event.message.context.MessageContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.syntaxdevteam.cleanerx.base.WordFilter

class FlectonePulseFormattingTest {

    @Test
    fun `listener zwraca zdarzenie z ocenzurowanym kontekstem FlectonePulse`() {
        val wordFilter = mockk<WordFilter>()
        every { wordFilter.censorMessage("brzydkie slowo", true) } returns "*************"
        val censoredContext = mockk<MessageContext>()
        every { censoredContext.message() } returns "*************"
        val context = mockk<MessageContext>()
        every { context.message() } returns "brzydkie slowo"
        every { context.isFlag(MessageFlag.PLAYER_MESSAGE) } returns true
        every { context.withMessage("*************") } returns censoredContext
        val event = MessageFormattingEvent(context)

        val result = FlectonePulseListener(wordFilter) { true }.onMessageFormatting(event)

        assertEquals("*************", result.context().message())
    }

    @Test
    fun `cenzuruje wiadomosc gracza`() {
        val result = censorFlectonePulseMessage(
            message = "to jest brzydkie slowo",
            isPlayerMessage = true,
            fullCensorship = true
        ) { text, _ -> text.replace("brzydkie slowo", "*************") }

        assertEquals("to jest *************", result)
    }

    @Test
    fun `nie zmienia szablonu ani wiadomosci systemowej`() {
        val result = censorFlectonePulseMessage(
            message = "<prefix><player>: <message>",
            isPlayerMessage = false,
            fullCensorship = true
        ) { _, _ -> error("Cenzor nie powinien zostać wywołany") }

        assertEquals("<prefix><player>: <message>", result)
    }

    @Test
    fun `przekazuje tryb czesciowej cenzury`() {
        var receivedFullCensorship: Boolean? = null
        censorFlectonePulseMessage("tekst", true, false) { text, fullCensorship ->
            receivedFullCensorship = fullCensorship
            text
        }

        assertEquals(false, receivedFullCensorship)
    }
}

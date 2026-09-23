package pl.syntaxdevteam.cleanerx.integration

import net.flectone.pulse.annotation.Pulse
import net.flectone.pulse.constant.MessageFlag
import net.flectone.pulse.listener.PulseListener
import net.flectone.pulse.model.event.Event
import net.flectone.pulse.model.event.message.MessageFormattingEvent
import pl.syntaxdevteam.cleanerx.base.WordFilter

internal class FlectonePulseListener(
    private val wordFilter: WordFilter,
    private val fullCensorship: () -> Boolean
) : PulseListener {

    @Pulse(priority = Event.Priority.LOWEST)
    fun onMessageFormatting(event: MessageFormattingEvent): MessageFormattingEvent {
        val context = event.context()
        val censored = censorFlectonePulseMessage(
            message = context.message(),
            isPlayerMessage = context.isFlag(MessageFlag.PLAYER_MESSAGE),
            fullCensorship = fullCensorship(),
            censor = wordFilter::censorMessage
        )
        if (censored == context.message()) return event

        return event.withContext(context.withMessage(censored))
    }
}

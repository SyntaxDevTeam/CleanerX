package pl.syntaxdevteam.cleanerx.integration

internal data class FlectonePulseFormattingRewrite(
    val message: String,
    val userMessage: String,
    val changed: Boolean
)

internal fun rewriteFlectonePulseFormatting(
    message: String,
    userMessage: String,
    isPlayerMessage: Boolean,
    fullCensorship: Boolean,
    censor: (String, Boolean) -> String
): FlectonePulseFormattingRewrite {
    var updatedMessage = message
    var updatedUserMessage = userMessage
    var changed = false

    if (isPlayerMessage && message.isNotBlank()) {
        val censoredMessage = censor(message, fullCensorship)
        if (censoredMessage != message) {
            updatedMessage = censoredMessage
            changed = true
        }
    }

    if (userMessage.isNotBlank()) {
        val censoredUserMessage = censor(userMessage, fullCensorship)
        if (censoredUserMessage != userMessage) {
            updatedUserMessage = censoredUserMessage
            changed = true
        }
    }

    return FlectonePulseFormattingRewrite(
        message = updatedMessage,
        userMessage = updatedUserMessage,
        changed = changed
    )
}

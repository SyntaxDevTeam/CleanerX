package pl.syntaxdevteam.cleanerx.integration

internal fun censorFlectonePulseMessage(
    message: String,
    isPlayerMessage: Boolean,
    fullCensorship: Boolean,
    censor: (String, Boolean) -> String
): String {
    if (!isPlayerMessage || message.isBlank()) return message
    return censor(message, fullCensorship)
}

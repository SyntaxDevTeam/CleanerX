package pl.syntaxdevteam.cleanerx.commands

internal fun parseExpressionArgument(args: Array<out String>, startIndex: Int = 1): String {
    if (startIndex >= args.size) {
        return ""
    }

    return args
        .drop(startIndex)
        .joinToString(" ")
        .trim()
        .removeEnclosingQuotes()
        .trim()
}

private fun String.removeEnclosingQuotes(): String {
    if (length < 2) {
        return this
    }

    val first = first()
    val last = last()
    val quoted = when (first) {
        '"' -> last == '"'
        '\'' -> last == '\''
        '“' -> last == '”'
        '„' -> last == '”' || last == '“'
        else -> false
    }

    if (!quoted) {
        return this
    }

    return substring(1, lastIndex)
        .replace("\\\"", "\"")
        .replace("\\'", "'")
        .replace("\\\\", "\\")
}

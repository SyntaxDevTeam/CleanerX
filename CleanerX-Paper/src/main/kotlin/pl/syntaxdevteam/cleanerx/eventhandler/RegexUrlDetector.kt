package pl.syntaxdevteam.cleanerx.eventhandler

/** Retained for compatibility; all URL checks use the same host validation. */
class RegexUrlDetector : UrlDetector by UriUrlDetector()

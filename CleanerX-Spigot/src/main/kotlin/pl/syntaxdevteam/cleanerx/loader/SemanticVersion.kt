package pl.syntaxdevteam.cleanerx.loader

data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {
        return when {
            major != other.major -> major - other.major
            minor != other.minor -> minor - other.minor
            else -> patch - other.patch
        }
    }

    companion object {
        fun parse(version: String): SemanticVersion {
            val parts = version.split(".").map { it.toIntOrNull() ?: 0 }
            val major = parts.getOrElse(0) { 0 }
            val minor = parts.getOrElse(1) { 0 }
            val patch = parts.getOrElse(2) { 0 }
            return SemanticVersion(major, minor, patch)
        }
    }
}

rootProject.name = "CleanerX"
include("CleanerX-Paper", "CleanerX-Spigot")

pluginManagement {
    repositories {
        // Twoje repozytoria z pluginami:
        maven("https://nexus.syntaxdevteam.pl/repository/maven-releases/")
        maven("https://nexus.syntaxdevteam.pl/repository/maven-snapshots/")

        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("plugin.lombok") version "2.4.20"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
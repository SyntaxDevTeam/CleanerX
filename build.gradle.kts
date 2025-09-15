plugins {
    kotlin("jvm") version "2.2.20"
}

group = "pl.syntaxdevteam"
version = "1.5.2"

allprojects {
    repositories {
        mavenCentral()
    }
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds both Paper and Spigot versions of the plugin"
    dependsOn(":CleanerX-Paper:build", ":CleanerX-Spigot:build")
}


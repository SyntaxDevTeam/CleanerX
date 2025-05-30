plugins {
    kotlin("jvm") version "2.2.0-RC"
}

group = "pl.syntaxdevteam"
version = "1.5.1-DEV"

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


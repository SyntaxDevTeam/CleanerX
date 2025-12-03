plugins {
    kotlin("jvm") version "2.2.21"
}

group = "pl.syntaxdevteam"
version = "1.5.4"

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


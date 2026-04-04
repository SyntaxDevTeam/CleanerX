plugins {
    kotlin("jvm") version "2.4.0-Beta1" apply false
    id("com.gradleup.shadow") version "9.4.1" apply false
    id("pl.syntaxdevteam.plugindeployer") version "1.0.5-R0.1-SNAPSHOT" apply false
}

group = "pl.syntaxdevteam"
version = "1.5.6"
description = "A sophisticated plugin designed to filter and replace inappropriate language with censored alternatives or remove it entirely, ensuring a clean and respectful gaming environment."

allprojects {
    group = rootProject.group
    version = rootProject.version
    description = rootProject.description

    repositories {
        mavenCentral()
    }
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds both Paper and Spigot versions of the plugin"
    dependsOn(":CleanerX-Paper:build", ":CleanerX-Spigot:build")
}

tasks.register("deploySpigotOnly") {
    group = "deployment"
    description = "Deploys only the Spigot module artifact"
    dependsOn(":CleanerX-Spigot:deployPluginToSpigot")
}

tasks.register("deployPaperOnly") {
    group = "deployment"
    description = "Deploys only the Paper module artifact"
    dependsOn(":CleanerX-Paper:deployPluginToPaper")
}

tasks.register("deployFoliaOnly") {
    group = "deployment"
    description = "Deploys only the Folia module artifact"
    dependsOn(":CleanerX-Paper:deployPluginToFolia")
}

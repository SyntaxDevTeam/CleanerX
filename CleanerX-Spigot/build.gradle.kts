plugins {
    kotlin("jvm") version "2.2.0-RC"
    id("com.gradleup.shadow") version "9.0.0-beta15"
}

group = "pl.syntaxdevteam"
version = "1.5.1-DEV"
description = "A sophisticated plugin designed to filter and replace inappropriate language with censored alternatives or remove it entirely, ensuring a clean and respectful gaming environment."

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.4")
    compileOnly("com.google.code.gson:gson:2.13.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.21.0")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.21.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.21.0")
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.21.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}




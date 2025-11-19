plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.2.2"
}

group = "pl.syntaxdevteam"
version = "1.5.4-DEV"
description = "A sophisticated plugin designed to filter and replace inappropriate language with censored alternatives or remove it entirely, ensuring a clean and respectful gaming environment."

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.alessiodp.com/releases/") {
        name = "alessiodp"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.5")
    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-platform-api:4.4.1")
    compileOnly("net.kyori:adventure-platform-facet:4.4.1")
    compileOnly("net.kyori:adventure-api:4.25.0")
    compileOnly("net.kyori:examination-api:1.3.0")
    compileOnly("net.kyori:examination-string:1.3.0")
    compileOnly("net.kyori:adventure-key:4.25.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.25.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.25.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.25.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.25.0")
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.25.0")
    implementation("net.byteflux:libby-bukkit:1.3.1")
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




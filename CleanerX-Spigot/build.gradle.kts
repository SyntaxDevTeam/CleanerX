plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
}

group = "pl.syntaxdevteam"
version = "1.5.5"
description = "A sophisticated plugin designed to filter and replace inappropriate language with censored alternatives or remove it entirely, ensuring a clean and respectful gaming environment."

repositories {
    maven("https://nexus.syntaxdevteam.pl/repository/maven-snapshots/") //SyntaxDevTeam
    maven("https://nexus.syntaxdevteam.pl/repository/maven-releases/") //SyntaxDevTeam
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
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.5")
    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("net.kyori:adventure-key:4.25.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-platform-api:4.4.1")
    compileOnly("net.kyori:adventure-platform-facet:4.4.1")
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-json:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.26.1")
    compileOnly("net.kyori:examination-api:1.3.0")
    compileOnly("net.kyori:examination-string:1.3.0")
    compileOnly("net.kyori:option:1.1.0")
    compileOnly("pl.syntaxdevteam:core:1.2.8-R0.1-SNAPSHOT")
    compileOnly("pl.syntaxdevteam:messageHandler-spigot:1.1.0-R0.1-SNAPSHOT")
    compileOnly("pl.syntaxdevteam.punisher:PunisherX:1.6.0-DEV")
    //implementation(files("libs/SyntaxCore-1.2.6n-SNAPSHOT-all.jar"))
    //implementation(files("libs/MessageHandler-Spigot-1.0.4a-DEV-all.jar"))
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




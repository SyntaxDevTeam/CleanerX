plugins {
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "pl.syntaxdevteam"
version = "1.4.0"
description = "A sophisticated plugin designed to filter and replace inappropriate language with censored alternatives or remove it entirely, ensuring a clean and respectful gaming environment."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.4")
    compileOnly("com.google.code.gson:gson:2.12.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.18.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.18.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.18.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.18.0")
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.18.0")
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
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
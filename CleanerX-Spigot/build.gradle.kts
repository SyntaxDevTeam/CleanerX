plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    id("pl.syntaxdevteam.plugindeployer")
}

repositories {
    maven("https://nexus.syntaxdevteam.pl/repository/maven-snapshots/") //SyntaxDevTeam
    maven("https://nexus.syntaxdevteam.pl/repository/maven-releases/") //SyntaxDevTeam
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    maven(url = "https://central.sonatype.com/repository/maven-snapshots/") {
        name = "central-snapshots"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.alessiodp.com/releases/") {
        name = "alessiodp"
    }
    maven {
        name = "faststatsReleases"
        url = uri("https://repo.faststats.dev/releases")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.6")
    compileOnly("com.google.code.gson:gson:2.14.0")
    compileOnly("net.kyori:adventure-key:5.2.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-platform-api:4.4.1")
    compileOnly("net.kyori:adventure-platform-facet:4.4.1")
    compileOnly("net.kyori:adventure-api:5.2.0")
    compileOnly("net.kyori:adventure-text-serializer-json:5.2.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:5.2.0")
    compileOnly("net.kyori:adventure-text-minimessage:5.2.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:5.2.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:5.2.0")
    compileOnly("net.kyori:adventure-text-serializer-ansi:5.2.0")
    compileOnly("net.kyori:examination-api:1.3.0")
    compileOnly("net.kyori:examination-string:1.3.0")
    compileOnly("net.kyori:option:1.1.0")
    compileOnly("pl.syntaxdevteam:syntaxcore:1.4.1-R0.1-SNAPSHOT")
    compileOnly("pl.syntaxdevteam:messageHandler-spigot:1.2.0-R0.3-SNAPSHOT")
    compileOnly("pl.syntaxdevteam.punisher:PunisherX:1.6.1")
    implementation("net.byteflux:libby-bukkit:1.3.1")
    compileOnly("dev.faststats.metrics:bukkit:0.29.1")

    testImplementation(kotlin("test"))
    testImplementation("com.google.guava:guava:33.3.1-jre")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.3.0")

    testImplementation("dev.faststats.metrics:bukkit:0.29.1")
}

val targetJavaVersion = 25
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    build {
        dependsOn("shadowJar")
    }
    test {
        useJUnitPlatform()
    }
}

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

plugindeployer {
    paper { dir = "/home/debian/server/Paper/26.2/plugins" } //ostatnia wersja dla Paper
    folia { dir = "/home/debian/server/Folia/26.1.2/plugins" } //ostatnia wersja dla Folia
    spigot { dir = "/home/debian/server/Spigot/26.2/plugins" } //ostatnia wersja dla Spigot
}

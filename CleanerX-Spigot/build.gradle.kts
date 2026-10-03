import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.file.DuplicatesStrategy

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    id("pl.syntaxdevteam.plugindeployer")
    kotlin("plugin.lombok")
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
    compileOnly(libs.spigot.api)
    compileOnly(libs.aether.api)
    compileOnly(libs.snakeyaml)
    compileOnly(libs.gson)
    compileOnly(libs.adventure.key)
    compileOnly(libs.adventure.platform.bukkit)
    compileOnly(libs.adventure.platform.api)
    compileOnly(libs.adventure.platform.facet)
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.json)
    compileOnly(libs.adventure.legacy)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.adventure.gson)
    compileOnly(libs.adventure.plain)
    compileOnly(libs.adventure.ansi)
    compileOnly(libs.examination.api)
    compileOnly(libs.examination.string)
    compileOnly(libs.kyori.option)
    compileOnly(libs.syntaxcore)
    compileOnly(libs.messagehandler.spigot)
    compileOnly(libs.punisherx)
    compileOnly(libs.flectone.pulse)
    implementation(libs.libby.bukkit)
    compileOnly(libs.faststats.bukkit)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.guava)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)

    testImplementation(libs.faststats.bukkit)
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

val runtimeLibraryVersions = mapOf(
    "adventureVersion" to libs.versions.adventure.spigot.get(),
    "adventurePlatformVersion" to libs.versions.adventure.platform.get(),
    "examinationVersion" to libs.versions.examination.get(),
    "optionVersion" to libs.versions.kyori.option.get(),
    "caffeineVersion" to libs.versions.caffeine.get(),
    "boostedYamlVersion" to libs.versions.boosted.yaml.get(),
    "syntaxcoreVersion" to libs.versions.syntaxcore.get(),
    "messagehandlerVersion" to libs.versions.messagehandler.get(),
    "gsonVersion" to libs.versions.gson.get(),
    "snakeyamlVersion" to libs.versions.snakeyaml.get(),
    "aetherVersion" to libs.versions.aether.get(),
    "faststatsVersion" to libs.versions.faststats.get(),
)

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props + runtimeLibraryVersions)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
    filesMatching("spigot-libraries.yml") {
        expand(runtimeLibraryVersions)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("CleanerX-Spigot")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    mergeServiceFiles()

    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    filesMatching("META-INF/*.kotlin_module") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    relocate("net.byteflux.libby", "pl.syntaxdevteam.cleanerx.libs.libby")

    dependencies {
        include(dependency("net.byteflux:libby-bukkit"))
        include(dependency("net.byteflux:libby-core"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8"))
    }
}

plugindeployer {
    paper { dir = "/home/debian/server/Paper/26.2/plugins" } //ostatnia wersja dla Paper
    folia { dir = "/home/debian/server/Folia/26.1.2/plugins" } //ostatnia wersja dla Folia
    spigot { dir = "/home/debian/server/Spigot/26.2/plugins" } //ostatnia wersja dla Spigot
}

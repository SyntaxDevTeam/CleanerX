import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    `maven-publish`
    id("xyz.jpenilla.run-paper")
    id("pl.syntaxdevteam.plugindeployer")
}

repositories {
    maven("https://nexus.syntaxdevteam.pl/repository/maven-snapshots/") //SyntaxDevTeam
    maven("https://nexus.syntaxdevteam.pl/repository/maven-releases/") //SyntaxDevTeam
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven {
        name = "faststatsReleases"
        url = uri("https://repo.faststats.dev/releases")
    }
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.aether.api)
    compileOnly(libs.snakeyaml)
    compileOnly(libs.gson)
    compileOnly(libs.adventure.paper.legacy)
    compileOnly(libs.adventure.paper.minimessage)
    compileOnly(libs.adventure.paper.gson)
    compileOnly(libs.adventure.paper.plain)
    compileOnly(libs.adventure.paper.ansi)
    compileOnly(libs.syntaxcore)
    compileOnly(libs.messagehandler.paper)
    compileOnly(libs.punisherx)
    compileOnly(libs.flectone.pulse)
    compileOnly(libs.faststats.bukkit)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.flectone.pulse)
    testImplementation(libs.paper.api)
    testImplementation(libs.syntaxcore)
    testImplementation(libs.messagehandler.paper)
    testImplementation(libs.faststats.bukkit)
    testRuntimeOnly(libs.slf4j.simple)

}

val targetJavaVersion = 25
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks{
    build {
        dependsOn("shadowJar")
    }
    runServer {
        minecraftVersion("26.1.2")
        runDirectory(file("run/paper"))
    }
    runPaper.folia.registerTask()

    test {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    }

}


val runtimeLibraryVersions = mapOf(
    "aetherVersion" to libs.versions.aether.get(),
    "snakeyamlVersion" to libs.versions.snakeyaml.get(),
    "gsonVersion" to libs.versions.gson.get(),
    "caffeineVersion" to libs.versions.caffeine.get(),
    "syntaxcoreVersion" to libs.versions.syntaxcore.get(),
    "messagehandlerVersion" to libs.versions.messagehandler.get(),
    "faststatsVersion" to libs.versions.faststats.get(),
)

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props + runtimeLibraryVersions)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
    filesMatching("paper-libraries.yml") {
        expand(runtimeLibraryVersions)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("CleanerX-Paper")
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
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}


publishing {
    publications {
        create<MavenPublication>("cleanerx") {
            artifactId = "cleanerx"
            artifact(tasks.named("shadowJar").get()) {
                classifier = null
            }
            artifact(sourcesJar.get())

            pom {
                name.set("CleanerX")
                description.set(project.description)
                url.set("https://github.com/SyntaxDevTeam/CleanerX")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("WieszczY85")
                        name.set("WieszczY")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "Nexus"
            val releasesRepoUrl = uri("https://nexus.syntaxdevteam.pl/repository/maven-releases/")
            val snapshotsRepoUrl = uri("https://nexus.syntaxdevteam.pl/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = findProperty("nexusUser")?.toString()
                password = findProperty("nexusPassword")?.toString()
            }
        }
    }
}
plugindeployer {
    paper { dir = "/home/debian/server/Paper/26.2/plugins" } //ostatnia wersja dla Paper
    folia { dir = "/home/debian/server/Folia/26.1.2/plugins" } //ostatnia wersja dla Folia
    spigot { dir = "/home/debian/server/Spigot/26.2/plugins" } //ostatnia wersja dla Spigot
}

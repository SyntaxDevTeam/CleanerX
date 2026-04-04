import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    `maven-publish`
    id("xyz.jpenilla.run-paper") version "3.0.2"
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
    }

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.1.build.+")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.6")
    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.26.1")
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.26.1")
    compileOnly("pl.syntaxdevteam:core:1.3.0-R0.2-SNAPSHOT")
    compileOnly("pl.syntaxdevteam:messageHandler-paper:1.1.2-R0.1-SNAPSHOT")
    compileOnly("pl.syntaxdevteam.punisher:PunisherX:1.6.1")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("io.papermc.paper:paper-api:26.1.1.build.+")
    testImplementation("pl.syntaxdevteam:core:1.3.0-R0.1.2-SNAPSHOT")
    testImplementation("pl.syntaxdevteam:messageHandler-paper:1.1.2-R0.1-SNAPSHOT")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")

}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks{
    build {
        dependsOn("shadowJar")
    }
    runServer {
        minecraftVersion("26.1.1")
        runDirectory(file("run/paper"))
    }
    runPaper.folia.registerTask()

    test {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    }
}


tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

val sourcesJar by tasks.registering(Jar::class) {
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
    paper { dir = "/home/debian/server/Paper/26.1.1/plugins" } //ostatnia wersja dla Paper
    folia { dir = "/home/debian/server/Folia/1.21.11/plugins" } //ostatnia wersja dla Folia
    spigot { dir = "/home/debian/server/Spigot/26.1.1/plugins" } //ostatnia wersja dla Spigot
}

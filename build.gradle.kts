import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    kotlin("jvm") version "2.0.20-RC2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"

}

group = "pl.syntaxdevteam"
version = "1.0"

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
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
hangarPublish {
    publications {
        register("CleanerX") {

            apiKey.set(System.getenv("6b1b6f54-8a72-4b6d-8aee-dc6caace71e8.e5bbf911-67a3-4bf7-be8a-00f5366251e6"))
            channel.set("Snapshot")
            id.set("SyntaxDevTeam/CleanerX")
            version = project.version.toString()
            platforms {
                paper {
                    jar.set(tasks.jar.flatMap { it.archiveFile })
                    platformVersions.set(listOf("1.21", "1.21.1"))
                }
            }
        }
    }
}

hangarPublish {
    publications.register("Plugin") {
        version.set(project.version as String)
        channel.set("Snapshot")
        id.set("SyntaxDevTeam/CleanerX")
        apiKey.set(System.getenv("6b1b6f54-8a72-4b6d-8aee-dc6caace71e8.e5bbf911-67a3-4bf7-be8a-00f5366251e6"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                val versions: List<String> = (property("paperVersion") as String)
                    .split(",")
                    .map { it.trim() }
                platformVersions.set(versions)
                dependencies {
                    hangar("Maintenance") {
                        required.set(false)
                    }
                    url("Debuggery", "https://github.com/PaperMC/Debuggery") {
                        required.set(true)
                    }
                }
            }
        }
    }
}








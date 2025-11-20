package pl.syntaxdevteam.cleanerx.loader

import net.byteflux.libby.BukkitLibraryManager
import net.byteflux.libby.Library
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Handles runtime dependency downloads using Libby.
 *
 * This loader ensures that libraries which are marked as compileOnly in the build script
 * are downloaded and available before the rest of the plugin is initialised.
 */
class LibraryLoader(private val plugin: JavaPlugin) {

    private val libraryManager = BukkitLibraryManager(plugin)

    /**
     * Downloads and loads all runtime libraries required by the plugin.
     * Currently, this ensures that MiniMessage is always available on Spigot servers.
     */
    fun loadRuntimeLibraries() {
        libraryManager.addMavenCentral()
        libraryManager.addRepository("https://nexus.syntaxdevteam.pl/repository/maven-releases/")
        libraryManager.addRepository("https://nexus.syntaxdevteam.pl/repository/maven-snapshots/")

        val runtimeLibraries = loadLibrariesFromConfig()

        if (runtimeLibraries.isEmpty()) {
            plugin.logger.warning("[CleanerX] No runtime libraries configured in $LIBRARIES_RESOURCE")
            return
        }

        for ((coordinates, library) in runtimeLibraries) {
            try {
                libraryManager.loadLibrary(library)
                plugin.logger.info("[CleanerX] Loaded runtime library: $coordinates")
            } catch (exception: Exception) {
                plugin.logger.severe("[CleanerX] Failed to load runtime library $coordinates: ${exception.message}")
                throw IllegalStateException("Unable to load runtime library $coordinates", exception)
            }
        }
    }

    private fun loadLibrariesFromConfig(): List<Pair<String, Library>> {
        val resourceStream = plugin.getResource(LIBRARIES_RESOURCE)
            ?: throw IllegalStateException("Missing $LIBRARIES_RESOURCE resource in plugin jar")

        val configuredLibraries = resourceStream.use { inputStream ->
            val config = YamlConfiguration.loadConfiguration(
                InputStreamReader(inputStream, StandardCharsets.UTF_8)
            )

            config.getStringList("libraries")
        }
        if (configuredLibraries.isEmpty()) {
            plugin.logger.warning("[CleanerX] No libraries defined under 'libraries' in $LIBRARIES_RESOURCE")
            return emptyList()
        }

        return configuredLibraries.mapNotNull { coordinates ->
            parseLibrary(coordinates)
        }
    }

    private fun parseLibrary(coordinates: String): Pair<String, Library>? {
        val parts = coordinates.split(":")
        if (parts.size != 3) {
            plugin.logger.severe("[CleanerX] Invalid library coordinates '$coordinates' in $LIBRARIES_RESOURCE. Expected format group:artifact:version")
            return null
        }

        val (groupId, artifactId, version) = parts
        val library = Library.builder()
            .groupId(groupId)
            .artifactId(artifactId)
            .version(version)
            .build()

        return coordinates to library
    }

    companion object {
        private const val LIBRARIES_RESOURCE = "spigot-libraries.yml"
    }
}
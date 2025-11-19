package pl.syntaxdevteam.cleanerx.common

import net.byteflux.libby.BukkitLibraryManager
import net.byteflux.libby.Library
import org.bukkit.plugin.java.JavaPlugin

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
     * Currently this ensures that MiniMessage is always available on Spigot servers.
     */
    fun loadRuntimeLibraries() {
        libraryManager.addMavenCentral()

        val runtimeLibraries = listOf(
            "net.kyori:adventure-text-minimessage:4.25.0" to Library.builder()
                .groupId("net.kyori")
                .artifactId("adventure-text-minimessage")
                .version("4.25.0")
                .build()
        )

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
}

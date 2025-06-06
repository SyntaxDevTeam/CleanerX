package pl.syntaxdevteam.cleanerx.common

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import pl.syntaxdevteam.cleanerx.CleanerX
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Suppress("UnstableApiUsage")
class UpdateChecker(private val plugin: CleanerX) {

    private val hangarApiUrl = "https://hangar.papermc.io/api/v1/projects/${plugin.description.name}/versions"
    private val pluginUrl = "https://hangar.papermc.io/SyntaxDevTeam/${plugin.description.name}"
    private val gson = Gson()

    fun checkForUpdates() {
        if (!plugin.config.getBoolean("checkForUpdates", true)) {
            plugin.logger.debug("Update check is disabled in the config.")
            return
        }

        CompletableFuture.runAsync {
            try {
                val uri = URI(hangarApiUrl)
                val url = uri.toURL()
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseBody = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                    val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                    val versions = jsonObject.getAsJsonArray("result")
                    val latestVersion = versions.firstOrNull()?.asJsonObject
                    if (latestVersion != null && isNewerVersion(latestVersion.get("name").asString, plugin.description.version)) {
                        notifyUpdate(latestVersion)
                        if (plugin.config.getBoolean("autoDownloadUpdates", false)) {
                            downloadUpdate(latestVersion)
                        }
                    } else {
                        plugin.logger.success("Your version is up to date")
                    }
                } else {
                    plugin.logger.warning("Failed to check for updates: $responseCode")
                }
            } catch (e: Exception) {
                plugin.logger.warning("An error occurred while checking for updates: ${e.message}")
            }
        }.orTimeout(10, TimeUnit.SECONDS).exceptionally { e ->
            plugin.logger.warning("Update check timed out or failed: ${e.message}")
            null
        }
    }

    private fun isNewerVersion(latestVersion: String, currentVersion: String): Boolean {
        val latestParts = latestVersion.split("-", limit = 2)
        val currentParts = currentVersion.split("-", limit = 2)

        val latestBase = latestParts[0].split(".")
        val currentBase = currentParts[0].split(".")

        for (i in latestBase.indices) {
            val latestPart = latestBase.getOrNull(i)?.toIntOrNull() ?: 0
            val currentPart = currentBase.getOrNull(i)?.toIntOrNull() ?: 0

            if (latestPart > currentPart) {
                return true
            } else if (latestPart < currentPart) {
                return false
            }
        }

        if (latestParts.size > 1 && currentParts.size > 1) {
            return latestParts[1] > currentParts[1]
        }

        return latestParts.size > currentParts.size
    }

    private fun notifyUpdate(version: JsonObject) {
        val versionName = version.get("name").asString
        val channel = version.getAsJsonObject("channel").get("name").asString
        val message = when (channel) {
            "Release" -> "<green>New release version <bold>$versionName</bold> is available on <click:open_url:'$pluginUrl'>Hangar</click>!"
            "Snapshot" -> "<yellow>New snapshot version <bold>$versionName</bold> is available on <click:open_url:'$pluginUrl'>Hangar</click>!"
            else -> "<blue>New version <bold>$versionName</bold> is available on <click:open_url:'$pluginUrl'>Hangar</click>!"
        }
        val component = MiniMessage.miniMessage().deserialize(message)
        plugin.logger.success("New release version $versionName is available on $pluginUrl")
        notifyAdmins(message)
    }

    private fun downloadUpdate(version: JsonObject) {
        val versionName = version.get("name").asString
        val downloadUrl = version.getAsJsonObject("downloads").getAsJsonObject("PAPER").get("downloadUrl").asString
        val fileName = version.getAsJsonObject("downloads").getAsJsonObject("PAPER").getAsJsonObject("fileInfo").get("name").asString
        val newFile = File(plugin.dataFolder.parentFile, fileName)
        val currentFile = plugin.getPluginFile()

        try {
            val uri = URI(downloadUrl)
            val url = uri.toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                url.openStream().use { input ->
                    newFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                plugin.logger.success("Downloaded new version $versionName to ${newFile.absolutePath}")
                if (currentFile.exists() && currentFile != newFile) {
                    currentFile.delete()
                    plugin.logger.success("Deleted old version ${currentFile.name}. Restart your server to enjoy the latest plugin version")
                }
            } else {
                plugin.logger.warning("Failed to download new version $versionName: $responseCode")
            }
        } catch (e: Exception) {
            plugin.logger.warning("Failed to download new version $versionName: ${e.message}")
        }
    }

    private fun notifyAdmins(message: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("${plugin.description.name}.update.notify")) {
                player.sendMessage(message)
            }
        }
    }
}

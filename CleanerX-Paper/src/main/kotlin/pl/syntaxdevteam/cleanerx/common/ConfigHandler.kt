package pl.syntaxdevteam.cleanerx.common

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import pl.syntaxdevteam.cleanerx.CleanerX

class ConfigHandler(private val plugin: CleanerX) {

    fun verifyAndUpdateConfig() {
        val confFile = File(plugin.dataFolder, "config.yml")
        val defaultConfStream = plugin.getResource("config.yml")

        if (defaultConfStream == null) {
            plugin.logger.err("Default $confFile file not found in plugin resources!")
            return
        }

        val defaultConfig = YamlConfiguration.loadConfiguration(defaultConfStream.reader())
        val currentConfig = YamlConfiguration.loadConfiguration(confFile)

        var updated = false
        val nonMergeableSections = setOf("swear-word-thresholds")

        fun synchronizeSections(defaultSection: ConfigurationSection, currentSection: ConfigurationSection, path: String = "") {
            for (key in defaultSection.getKeys(false)) {
                val currentPath = if (path.isEmpty()) key else "$path.$key"

                if (!currentSection.contains(key)) {
                    currentSection[key] = defaultSection[key]
                    updated = true
                } else if (defaultSection.isConfigurationSection(key)) {
                    if (currentPath in nonMergeableSections) {
                        continue
                    }

                    synchronizeSections(
                        defaultSection.getConfigurationSection(key)!!,
                        currentSection.getConfigurationSection(key)!!,
                        currentPath
                    )
                }
            }
        }

        synchronizeSections(defaultConfig, currentConfig)

        if (updated) {
            plugin.logger.success("Updating $confFile file with missing entries.")
            currentConfig.save(confFile)
        }
    }
}

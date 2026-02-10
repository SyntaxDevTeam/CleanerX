package pl.syntaxdevteam.cleanerx.common

import io.mockk.every
import io.mockk.mockk
import org.bukkit.configuration.file.YamlConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import pl.syntaxdevteam.cleanerx.CleanerX
import pl.syntaxdevteam.core.logging.Logger
import java.io.ByteArrayInputStream
import java.nio.file.Path

class ConfigHandlerTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `verifyAndUpdateConfig dodaje brakujace klucze i zachowuje istniejace`() {
        val dataDir = tempDir.toFile()
        val configFile = dataDir.resolve("config.yml")
        configFile.writeText(
            """
            language: pl
            settings:
              censorship: partial
            """.trimIndent()
        )

        val defaultConfig =
            """
            language: en
            block-links: true
            settings:
              censorship: full
              punish-after: 3
            """.trimIndent()

        val plugin = mockk<CleanerX>(relaxed = true)
        every { plugin.dataFolder } returns dataDir
        every { plugin.getResource("config.yml") } returns ByteArrayInputStream(defaultConfig.toByteArray())
        every { plugin.logger } returns mockk<Logger>(relaxed = true)

        ConfigHandler(plugin).verifyAndUpdateConfig()

        val updated = YamlConfiguration.loadConfiguration(configFile)
        assertEquals("pl", updated.getString("language"))
        assertEquals("partial", updated.getString("settings.censorship"))
        assertTrue(updated.getBoolean("block-links"))
        assertEquals(3, updated.getInt("settings.punish-after"))
    }
}

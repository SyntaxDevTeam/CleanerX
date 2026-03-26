package pl.syntaxdevteam.cleanerx.loader

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.bukkit.Bukkit
import org.bukkit.Server
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.syntaxdevteam.cleanerx.CleanerX

class SemanticVersionAndVersionCheckerTest {

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `semantic version parse i compare dziala poprawnie`() {
        assertEquals(SemanticVersion(1, 21, 11), SemanticVersion.parse("git-Paper-1.21.11-R0.1-SNAPSHOT"))
        assertTrue(SemanticVersion(1, 21, 11) > SemanticVersion(1, 21, 10))
        assertTrue(SemanticVersion(1, 21, 11).isBetween(SemanticVersion(1, 21, 0), SemanticVersion(1, 21, 99)))
        assertFalse(SemanticVersion(1, 20, 6).isBetween(SemanticVersion(1, 21, 0), null))
    }

    @Test
    fun `version checker poprawnie rozpoznaje wspierana i niewspierana wersje`() {
        mockkStatic(Bukkit::class)
        val server = mockk<Server>()
        every { Bukkit.getServer() } returns server

        every { server.bukkitVersion } returns "1.21.11-R0.1-SNAPSHOT"
        val checker = VersionChecker(mockk<CleanerX>(relaxed = true))
        assertEquals("1.21.11", checker.getServerVersion())
        assertTrue(checker.isSupported())
        assertTrue(checker.isAtLeast("1.21.10"))

        every { server.bukkitVersion } returns "1.19.4-R0.1-SNAPSHOT"
        assertFalse(checker.isSupported())
        assertFalse(checker.isAtLeast("1.20.6"))
    }

    @Test
    fun `supports approved legacy versions`() {
        assertTrue(VersionChecker.isVersionSupported("1.20.6"))
        assertTrue(VersionChecker.isVersionSupported("1.21"))
        assertTrue(VersionChecker.isVersionSupported("1.21.0"))
        assertTrue(VersionChecker.isVersionSupported("1.21.11"))
    }

    @Test
    fun `supports approved year-based versions`() {
        assertTrue(VersionChecker.isVersionSupported("26.1"))
        assertTrue(VersionChecker.isVersionSupported("26.1.0"))
    }

    @Test
    fun `rejects versions that are not explicitly approved`() {
        assertFalse(VersionChecker.isVersionSupported("1.20.5"))
        assertFalse(VersionChecker.isVersionSupported("1.22"))
        assertFalse(VersionChecker.isVersionSupported("26.0"))
        assertFalse(VersionChecker.isVersionSupported("26.2"))
        assertFalse(VersionChecker.isVersionSupported("27.1"))
    }
}

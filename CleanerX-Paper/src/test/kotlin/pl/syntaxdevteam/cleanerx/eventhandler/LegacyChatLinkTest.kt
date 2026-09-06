@file:Suppress("DEPRECATION")

package pl.syntaxdevteam.cleanerx.eventhandler

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import pl.syntaxdevteam.cleanerx.CleanerX
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LegacyChatLinkTest {
    @Test
    fun `LPC allows sentence punctuation and sends a formatted component for blocked links`() {
        val plugin = mockk<CleanerX>(relaxed = true)
        val config = YamlConfiguration().apply { set("block-links", true) }
        every { plugin.config } returns config
        every { plugin.punisherXApi } returns null
        val warning = Component.text("Links are blocked", NamedTextColor.RED)
        val messageHandler = plugin.messageHandler
        every { messageHandler.stringMessageToComponent("error", "no-link") } returns warning
        val player = mockk<Player>(relaxed = true)
        val listener = CleanerXChat(plugin, mockk(relaxed = true), false, mockk(relaxed = true), true, false)

        val sentence = AsyncPlayerChatEvent(true, player, "ok.", mutableSetOf())
        listener.onLegacyChat(sentence)
        assertFalse(sentence.isCancelled)
        verify(exactly = 0) { player.sendMessage(any<Component>()) }

        val link = AsyncPlayerChatEvent(true, player, "example.com", mutableSetOf())
        listener.onLegacyChat(link)
        assertTrue(link.isCancelled)
        verify(exactly = 1) { player.sendMessage(warning) }
        verify(exactly = 0) { messageHandler.stringMessageToString("error", "no-link") }
    }
}

package pl.syntaxdevteam.cleanerx.common

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import pl.syntaxdevteam.cleanerx.CleanerX

@Suppress("UnstableApiUsage")
class Logger(plugin: CleanerX, private val debugMode: Boolean) {
    private val plName = plugin.description.name
    private val plVer = plugin.description.version
    private val serverVersion = plugin.server.version
    private val serverName = plugin.server.name

    private fun clear(s: String?) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', s!!))
    }

    fun success(s: String) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[$plName] &a&l$s&r"))
    }

    fun info(s: String) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[$plName] $s"))
    }

    fun warning(s: String) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[$plName] &6$s&r"))
    }

    fun err(s: String) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[$plName] &c$s&r"))
    }

    fun severe(s: String) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[$plName] &c&l$s&r"))
    }

    fun log(s: String) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[$plName] $s"))
    }

    fun debug(s: String) {
        if (debugMode) {
            Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes(
                    '&',
                    "[$plName] [DEBUG] &e&l$s"
                )
            )
        }
    }

    fun pluginStart(pluginsByPriority: List<Pair<String, String>>) {
        clear("")
        clear("&9    __                   __        ___            ")
        clear("&9   (_      _  |_  _     |  \\  _     |   _  _   _  ")
        clear("&9   __) \\/ | ) |_ (_| )( |__/ (- \\/  |  (- (_| ||| ")
        clear("&9       /                                          ")
        clear("&9           ... is proud to present and enable:")
        clear("&9                     &f * &f&l${plName} v${plVer}")
        for ((pluginName, pluginVersion) in pluginsByPriority) {
            clear("&9                     &f * $pluginName v$pluginVersion")
        }
        clear("&9                 utilizing all the optimizations of your server $serverName $serverVersion!         ")
        clear("")
        clear("&c    Join our Discord! &6&lhttps://discord.gg/Zk6mxv7eMh")
        clear("")
        clear("")
    }
}


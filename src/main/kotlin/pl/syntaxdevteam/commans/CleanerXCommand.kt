package pl.syntaxdevteam.commans

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner
import org.jetbrains.annotations.NotNull
import pl.syntaxdevteam.CleanerX

/**
 * CleanerX command
 *
 * @property plugin
 * @constructor Create empty CleanerXCommand
 */
@Suppress("UnstableApiUsage", "DEPRECATION")
class CleanerXCommand(private val plugin: CleanerX) : BasicCommand {

    override fun execute(@NotNull stack: CommandSourceStack, @NotNull args: Array<String>) {
        val pluginMeta = (plugin as LifecycleEventOwner).pluginMeta
        val pdf = plugin.description
        if (args.isNotEmpty()) {
            when {
                args[0].equals("help", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.help")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#  <gold>Dostępne komendy dla " + pluginMeta.name + ":\n<gray>#\n#  <gold>/crx help <gray>- <white>Wyświetla ten monit.\n<gray>#  <gold>/crx version <gray>- <white>Pokazuje info pluginu. \n<gray>#  <gold>/crx reload <gray>- <white>Przeładowuje plik konfiguracyjny\n<gray>#\n#######################################")
                    } else {
                        stack.sender.sendMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                args[0].equals("version", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.version")) {
                        stack.sender.sendRichMessage("<gray>#######################################\n#\n#   <gold>→ <bold>" + pluginMeta.name + "</bold> ←\n<gray>#   <white>Autor: <bold><gold>" + pdf.authors + "</gold></bold>\n<gray>#   <white>WWW: <bold><gold><click:open_url:'" + pdf.website + "'>"  + pdf.website + "</click></gold></bold>\n<gray>#   <white>Wersja: <bold><gold>" + pluginMeta.version + "</gold></bold><gray>\n#\n#######################################")
                    } else {
                        stack.sender.sendRichMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                args[0].equals("reload", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.reload")) {
                        plugin.restartMySentinelTask()
                        stack.sender.sendRichMessage("<green>Plik konfiguracyjny został przeładowany.</green>")
                    } else {
                        stack.sender.sendRichMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                args[0].equals("addword", ignoreCase = true) -> {
                    if (stack.sender.hasPermission("CleanerX.addword")) {
                        if (args.size > 1) {
                            val newWord = args[1]
                            plugin.addBannedWord(newWord)
                            stack.sender.sendMessage("<green>Dodano nowe zakazane słowo: $newWord</green>")
                        } else {
                            stack.sender.sendMessage("<red>Proszę podać słowo do dodania.</red>")
                        }
                    } else {
                        stack.sender.sendMessage("Nie masz uprawnień do tej komendy.")
                    }
                }
                else -> {
                    stack.sender.sendRichMessage("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
                }
            }
        } else {
            stack.sender.sendRichMessage("<green>Wpisz /slx help aby sprawdzić dostępne komendy")
        }
    }
}

# CleanerX

CleanerX is an advanced plugin designed to filter and replace inappropriate language with censored alternatives or remove it entirely, ensuring a clean and respectful gaming environment. It allows you to configure automatic execution of commands, for example, to punish players who repeatedly use offensive language after a specified number of infractions. The plugin also blocks the ability to send links in chat, and if needed, you can clear the chat with a simple command.


## 🔹 Features

- ✅ Partial or complete word censorship depending on configuration
- ✅ Ability to block unwanted links
- ✅ Add custom words to the blacklist via command without needing to restart the server
- ✅ Add custom words to the whitelist via command without needing to restart the server
- ✅ Automatic execution of commands based on configuration – recommended integration with PunisherX
- ✅ Ability to clear chat using a command
- ✅ Plugin update notifications with an option to enable automatic updates
- ✅ Extensive configuration file for flexible settings. Check the default settings [here](https://github.com/SyntaxDevTeam/CleanerX/blob/main/src/main/resources/config.yml)
- ✅ Multi-language support via `messages_xx.yml` files

## ⚙️ Technical Information

- 🟢 **Designed for the latest Minecraft versions 1.20.6–1.21.4+**
- 🟢 **Optimized for Paper and its forks (Pufferfish, Purpur, Leaves)**
- 🟢 **Requires Java 21 or newer**
- 🟢 **Written in Kotlin – a modern, expressive, and safer alternative to Java**

If you have any questions, you might find answers on our [Discord](https://discord.gg/Q343kjA2YP).

## 🛠️ Commands and Permissions
| Command                               | Permission               | Description                                        |
|---------------------------------------|--------------------------|----------------------------------------------------|
| `/whitelistx <add/remove/list>`        | `cleanerx.cmd.whitelist` | Adds, removes or displays words from the whitelist |
| `/blacklistx <add/remove/list>`        | `cleanerx.cmd.balcklist` | Adds, removes or displays words from the blacklist |
| `/cleanx`                              | `cleanerx.cmd.clean`     | Clears the in-game chat                            |
| `/cleanerx help` or `/crx help`       | `cleanerx.cmd.crx`       | Displays a list of available commands              |
| `/cleanerx reload` or `/crx reload`   | `cleanerx.cmd.crx`    | Reloads the configuration file.                    |
| `/cleanerx version` or `/crx version` | `cleanerx.cmd.crx`   | Shows plugin info                                  |

## 📥 Download

- **The latest stable version is available on Hangar:**  [![Available on Hangar](https://img.shields.io/hangar/dt/CleanerX)](https://hangar.papermc.io/SyntaxDevTeam/CleanerX)
- **You can also build the latest development version from:**  [![Available on GitHub](https://img.shields.io/badge/GitHub.com-CleanerX-green)](https://github.com/SyntaxDevTeam/CleanerX)

## 🚀 Installation

1. Download the latest version of the plugin from the release section.
2. Place the JAR file into the `plugins` folder on your server.
3. Start your Minecraft server.
4. **Configuration:**  
   Open the `config.yml` file to customize settings such as the list of banned words or full censorship mode.

## 📞 Contact

If you have any questions or need support, feel free to reach out on our [Discord](https://discord.gg/Q343kjA2YP) or send us a direct message.

## 📜 License

This plugin is available under the **MIT License**. You can find the details in the LICENSE file.

---

**Thank you for using CleanerX! I hope it meets your expectations. 😊**

---

![syntaxdevteam_logo.png](assets/syntaxdevteam_logo.png)
---
<details>
<summary>Polska wersja README?</summary>

# CleanerX

CleanerX to zaawansowana wtyczka zaprojektowana do filtrowania i zastępowania nieodpowiedniego języka ocenzurowanymi alternatywami lub całkowitego usuwania go, zapewniając czyste i pełne szacunku środowisko gry. Pozwala na skonfigurowanie automatycznego wykonywanie poleceń, na przykład, aby ukarać graczy, którzy wielokrotnie używają obraźliwego języka po określonej liczbie przekleństw. Wtyczka blokuje również, możliwość wysyłania na czacie linków do stron, a w razie potrzeby możesz wyczyścić czat za pomocą polecenia.

Pełny spis komend i uprawnień znajdziesz [tutaj](https://github.com/SyntaxDevTeam/CleanerX/wiki) 

## Możliwości

* Cenzurowanie częściowe lub całkowite słów w zależności od konfiguracji
* Możliwość blokowania niechcianych linków
* Możliwość dodawania własnych słów do blacklisty za pomoca komendy bez konieczności restartu serwera
* Możliwość dodawania własnych słów do whitelisty za pomoca komendy bez konieczności restartu serwera
* Automatyczne wykonywanie poleceń w zależności od konfiguracji - zalecana współpraca z PunisherX
* Możliwość czyszczenia czatu za pomocą polecenia
* Powiadomienia o aktualizacjach wtyczki z opcją ustawienia automatycznych aktualizacji
* Rozbudowany plik konfiguracyjny do elastycznych ustawień. Sprawdź domyślne ustawienia [tutaj](https://github.com/SyntaxDevTeam/CleanerX/blob/main/src/main/resources/config.yml)
* Wsparcie dla wielu języków przez plik messages_xx.yml

## Informacje techniczne
* [x] CleanerX został zaprojektowany specjalnie pod najnowszą wersję Minecraft 1.20.6-1.21.4+
* [x] Napisany i zoptymalizowano pod silnik Paper oraz jego modyfikacje tj. Pufferfish, Purpur i Leaves
* [x] Wymaga Javy 21 lub nowszej, aby działać poprawnie
* [x] Napisany w nowoczesnym języku programowania Kotlin, który jest bardziej ekspresyjny i bezpieczny niż Java.

Jeśli masz jakieś pytania, być może znajdziesz na nie rozwiązanie na naszym [discordzie](https://discord.gg/Q343kjA2YP)

## Pobierz
* Zawsze aktualna wersja stabilna do pobrania na Hangar [![Available on Hangar](https://img.shields.io/hangar/dt/CleanerX)](https://hangar.papermc.io/SyntaxDevTeam/CleanerX)
* Możesz także samodzielnie zbudować wersję developerską z [![Available on GitHub](https://img.shields.io/badge/GitHub.com-CleanerX-green)](https://github.com/SyntaxDevTeam/CleanerX)


## Instalacja
* Pobierz najnowszą wersję pluginu z sekcji wydań.
* Umieść plik JAR w folderze plugins na swoim serwerze.
* Uruchom serwer Minecraft.
* Konfiguracja
  W pliku config.yml znajdziesz opcje konfiguracyjne, takie jak listę zakazanych słów czy tryb pełnej cenzury.

## Kontakt
Jeśli masz pytania lub potrzebujesz pomocy, śmiało skontaktuj się z nami na naszym [discordzie](https://discord.gg/Q343kjA2YP) lub napisz bezpośrednio na PM

## Licencja
Ten plugin jest dostępny na licencji MIT. Szczegóły znajdziesz w pliku LICENSE.

Dziękuję za korzystanie z CleanerX! Mam nadzieję, że spełni twoje oczekiwania. 😊
</details>

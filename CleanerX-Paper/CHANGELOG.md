# Changelog

## [1.5.5] - 2026-03-28
### Function changes:
- Add optional remote banned-words synchronization
  ```yaml
  banned-words-sync:
  enabled: false
  include-default-resource: true
  timeout-ms: 2500
  remote-sources: []
  ```
- Massively expand default banned words with multilingual entries


### Technical changes:
- Added PlayerQuitListener to reset swear count on player quit
- Refactor SwearCounter to use UUID
- Added legacy serializer compatibility methods across platforms
- Updated version detection to the new Minecraft versioning system.
- Improved PunisherX integration error handling
- Updated all dependencies and libraries to the latest versions

### Bug fixes:
- **HOTFIX**: fixed incorrectly displayed link notification formatting
- Fix: enhance update checker initialization
- Fix: fixed missing PunisherX class support


## [1.5.4] - 2026-02-15

### Function changes:
- Added support for Minecraft 1.21.11
- Added Hook for PunisherX 1.6+
  - The plugin checks if the censored text belongs to a muted person to avoid unnecessary counting of offenses.
- Added reset command for swear counter and update chat event handling
- Updated libraries and dependencies

### Bug fixes:
- Fixed downloading wrong versions of messaging dependencies
- Minor and major fixes


## [1.5.3] - 2025-11-14
* Added support for 1.21.9/10
* Improved message loading by using the latest MessageHandler library
* The API code has been slimmed down and optimized
*  Minor corrections
* Updating libraries and dependencies

## [1.5.2] - 2025-07-19
* Added support for 1.21.6/7/8
* **Added dual-mode link checking in chat**
* Minor corrections
* Updating libraries and dependencies

## [1.5.1] - 2025-06-06
* Added support for 1.21.5
* The project structure has been reorganized
* Updated libraries to the latest versions
* Changed the way messages are reloaded
* Minor fixes

## [1.5.0] - 2025-02-23
> 🚀 Long-awaited improved version with all feedback from players and server administrators taken into account!

### Functional Changes:
-  Replaced the simple word addition command with a full-featured version, allowing adding, removing, and viewing the current list of banned words in-game.
-  In response to multiple requests, added a **WHITELIST** to exclude words that may theoretically contain offensive words in normal expressions.
-  Added a command for the whitelist, enabling real-time addition, removal, and checking of the current list of whitelisted words.
-  Completely rewritten permission handling logic for all plugin components!  
   > **Attention server administrators! Be sure to review the new permission table!!**
-  Improved link detection logic.
-  Refactored multiple classes, and some were rewritten from scratch to improve performance.
-  **CleanerX no longer interferes with chat formatting** and does not conflict with specialized chat formatting plugins.
-  All static messages are now available in the language file, allowing translation into any language.
-  All messages fully support every popular text formatting style, and mixing styles no longer causes conflicts, thanks to the latest **SyntaxDevTeam message handling standard** based on Adventure libraries.
-  Updated all dependencies and classes to their latest versions for greater stability and performance.

## [1.3.1] - 04.12.2024
### Functional Changes:

* Added support for Minecraft 1.20.6 - 1.21.4
* Resolve conflicts in AsyncChatEvent handling between plugins
* Improvements in link detection logic
* Added new banned words
* Added extra debug
* Various bug fixes, both minor and major


### Technical Changes:
* Update dependency gradle to v8.11
* Update plugin org.jetbrains.kotlin.jvm to v2.1.0
* Directory reorganization adapted to SyntaxDevTeam standards

## [1.3.0] - 14.11.2024
### Functional Changes:

* Significant improvement in the logic for comparing words in the list of prohibited words.
* Added a command to clear the chat window /clean.
* Added the ability to block website links in the chat (enabled by default in the config.yml file).
* Added the ability to execute commands (e.g., punishing players) for repeated use of offensive words (enabled by default in the config.yml file), with the option to set how many words trigger the command and its content.
* Various bug fixes, both minor and major.

> **VERY IMPORTANT!** If you are updating CleanerX to a new version, to fully utilize the newly added features, you must delete the old config.yml file from the plugin folder and restart server.
### Technical Changes:
* Update dependency gradle to v8.10.2
* Update plugin org.jetbrains.kotlin.jvm to v2.1.0-RC

## [1.2.1] - 15.09.2024
* Added support for Minecraft 1.20.6 - 1.21.3
* Changed obsolete json-simple to Gson and rewrite PluginManager & UpdateChecker class
* Updated dependencies
* Updated kotlin version
* Less "aggressive" logo on console
* Updated plugin version
* Cleaning unnecessary files

## [1.2.0] - 15.09.2024
* Significant performance optimization and weight reduction

## [1.1.2-SNAPSHOT] - 04.09.2024
* Added auto-update

## [1.1.1] - 31.08.2024
* **Hotfix** for correct version comparison in the notification system

## [1.1.0] - 31.08.2024
* Added new version notifications
* Added option to disable new version notifications

**WARNING!**
**Be sure to delete your config file from the plugin folder to update the new options!**

## [1.0.4] - 28.08.2024
* Added backward compatibility for version 1.20.6

## [1.0.3] - 16.08.2024
* minor fixes
* update of deprecated methods
* added missing info to StatsCollector
* updated config file

## [1.0.2-SNAPSHOT] - 16.08.2024
- Added collection of plugin activation statistics for informational purposes for the author.
- Statistics are sent to an external server for plugin usage monitoring.
- Bug fixes and performance optimizations.

## [1.0.1-SNAPSHOT] - 15.08.2024
* Translated plugin classes from Polish to English for international compatibility
* Added support for Folia.
* Improved loading of `banned_words.yml` file from resources.
* Minor corrections

## [1.0] - 15.08.2024
- Initial release of CleanerX for Paper
- Basic chat filtering functionality with support for Minecraft 1.21-1.21.1
# Changelog

## [1.5.9] - Unreleased

### Compatibility

* Added support for Minecraft 26.2 and 26.3 on Spigot.

### Technical changes

* Updated to `messageHandler-spigot` 1.3.0-R0.4-SNAPSHOT.
* Removed duplicate Adventure classes from the plugin and runtime dependencies. MessageHandler supplies the core Adventure classes, and the loader adds the modules required by BukkitAudiences.
* Removed the standalone Caffeine download. MessageHandler and SyntaxCore contain the Caffeine classes they use.

## [1.5.8] - 2026-05-09

## What's Changed

### Functional Changes:
* Added support for [FlectonePulse](https://github.com/Flectone/FlectonePulse)

## Technical Changes:
* Update Gradle wrapper to version 9.5.0

## [1.5.7] - 2026-04-11

## Changelog
### Functional changes:
* Added backward compatibility with Java 21
* Added support for Minecraft version 1.20.6, 1.21 - 1.21.11, 26.1, 26.1.1/2
* A new revolutionary statistics system, FastStats, has been added

## [1.5.6] - 2026-04-04

Changes:
* **Added support for Minecraft 26.1.1**
* Centralize Gradle project metadata in root build
* Minor corrections
* Updated dependencies and libraries

## [1.5.5] - 2026-03-28
### Function changes:
- Added support for Minecraft **26.1**
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

## [1.5.4] - 2026-02-07

### Function changes:
- **Added support for the `LPC - Chat Formatter` plugin**
- Added support for Minecraft 1.21.11
- **Added automatic dependency loading**
  - The plugin will download and load libraries and dependencies itself
  - The plugin's weight has been significantly changed
- Added Hook for PunisherX 1.6+
  - The plugin checks if the censored text belongs to a muted person to avoid unnecessary counting of offenses.
- **Added reset command for swear counter and update chat event handling**
  ```command
    /crx reset <player>
  ```
- Updated libraries and dependencies

### Bug fixes:
- Fixed downloading wrong versions of messaging dependencies
- Minor and major fixes

## [1.5.4-SNAPSHOT] - 2026-02-08
### Function changes:
- Added support for Minecraft 1.21.11
- **Added automatic dependency loading**
  - The plugin will download and load libraries and dependencies itself
  - The plugin's weight has been significantly changed
- Added Hook for PunisherX 1.6+
  - The plugin checks if the censored text belongs to a muted person to avoid unnecessary counting of offenses.
- **Added reset command for swear counter and update chat event handling**
- Updated libraries and dependencies

### Bug fixes:
- Fixed downloading wrong versions of messaging dependencies
- Minor and major fixes

## [1.5.3] - 2025-11-14
* Added support for 1.21.9/10
* Removed unused UUIDManager class
* Minor corrections
* Updating libraries and dependencies

## [1.5.2] - 2025-07-12
* Added support for 1.21.6/7/8
* **Added dual-mode link checking in chat**
* Updated formatting in language files for Bukkit/Spigot servers
* Minor corrections
* Updating libraries and dependencies

## [1.5.1-DEV] - 2025-06-09

### What's Changed
* Added support for 1.21.5
* The project structure has been reorganized
* Updated libraries to the latest versions
* Changed the way messages are reloaded
* Minor fixes
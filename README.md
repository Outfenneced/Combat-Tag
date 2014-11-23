Combat Tag [![Build Status](https://travis-ci.org/Techcable/Combat-Tag.svg)](https://travis-ci.org/Techcable/Combat-Tag)
=============
Combat Tag is a bukkit plugin that prevents pvp loggers from getting away with logging


## Prerequisites
* [Citizens2](http://dev.bukkit.org/server-mods/citizens/)
* Java 7

## TODO List
- [ ] Backup NPC Implementation
- [ ] Reimplement Integration with other plugins

## Configuration

- **npcName**: The name of the npc to spawn in the event of combat logging
  - default: PvpLogger
  - player is replaced with the name of the combat logger
  - number is replaced with the npc number
  - If you don't include "player" or "number" the plugin will append the npcNumber to the end of the name
  - a plain "player" as npcName will show the player skin, but will cause an incompatibility with heroes

- **diabledWorlds**: worlds not to spawn npcs in
  - default: [exampleWorld,exampleWorld2]

- **Tag-Duration**: length of a combat-tag in seconds
  - default: 10

- **disabledCommands**: commands that are disabled while in combat
  - default: []

- **Enable-Debugging**: enable debug mode
  - default: false

- **Version**: Plugin Version
  - **DO NOT EDIT**

- **InstaKill**: Wether to kill (true) or spawn an npc (false) on combat-log
  - default: false (spawn an npc)

- **npcDespawnTime**: Time (in seconds) till the npc despawns
  - default: false (neverDespawn)
  - to never despawn set to -1
  - currently doesn't work

- **blockEditWhileTagged**: Wether people can place and break blocks while combat tagged
  - default: true

- **npcDieAfterTime**: when an npc despawns due to npc despawn time does the npc die (true) or does it despawn causing the player to keep their items (false)
  - default: false

- **tagMessageDamaged**: the message to send to defender when they are combat tagged
  - default: "You have been hit by [player]. Type /ct to check your remaining tag time."
  - [player] is replaced by the attacker's name

- **dontSpawnInWG**: don't spawn in worldguard regions with invincibility or disabled pvp
  - default: false
  - Currently Disabled (See TODO)

- **commandMessageTagged**: the message sent for /ct when you are tagged
  - default: "You are in combat for [time] seconds."
  - [time] is replaced with time remaining in combat

- **commandMessageNotTagged**: the message sent for /ct when you aren't tagged
  - default: "You are not currently in combat."

- **sendMessageWhenTagged**: send messages to players when they are tagged
  - default: true

- **blockTeleport**: block plugin teleportation
  - default: false
  - **WARNING**: THIS PREVENTS TELEPORTING AWAY BY OTHER PLAYERS

- **tagMessageDamager**: Sent to attacker on combat tag
  - default: "You have hit [player]. Type /ct to check your remaining tag time."
  - [player] is replaced with the defender

- **DropTagOnKick**: cause players to un-combat tag on kick
  - default: true

- **blockEnderPearl**: block ender pearl teleports when tagged
  - default: false

- **onlyDamagerTagger**: only combat tag the attacker
  - default: false

- **blockCreativeTagging**: players in creative can't tag other players
  - default: true

- **blockFlying**: prevents players from flying when combat tagged
  - default:false
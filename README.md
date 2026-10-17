# No Elytra During Combat (Fabric)

A pure Fabric mod port of the [NoElytraDuringCombat](https://github.com/Cvahotni/NoElytraDuringCombat) Paper plugin for **Minecraft 1.21.11**.

---

## Features

- **PvP Combat Detection**: Engaging in PvP combat (either hitting or being hit) triggers a combat timer for both participants.
- **Flight Prevention**:
  - Automatically cancels Elytra deployment attempts when in combat.
  - Immediately forces grounded flight termination if a player enters combat while mid-air.
- **Text Feedback Above Hotbar (Action Bar)**: Sends immediate overlay notification above the player's hotbar when Elytra flight is denied during combat, preventing chat clutter.
- **Configurable 30-Second Combat Timer**: Defaults to 30 seconds as configured in `config/noelytraduringcombat.json`.
- **Username Flight Whitelist**: Configurable list of usernames that bypass combat flight restrictions.
- **Operator / Admin Bypass**: Players with game master/operator permissions bypass restrictions.
- **Rate-Limited Notifications**: Configurable interval (`notifyInterval`, default 5.0s) prevents spam when players attempt to fly while in combat.
- **Death & Disconnect Handling**: Players who die or disconnect are immediately removed from combat tracking.
- **Admin Commands**:
  - `/nedcreload` (or `/nedc reload`): Reloads configuration from disk.
  - `/nedcsave` (or `/nedc save`): Saves active configuration to disk.

---

## Configuration

Configuration is located at `config/noelytraduringcombat.json`:

```json
{
  "combatTimeInSeconds": 30,
  "notifyInterval": 5.0,
  "sendToActionBar": true,
  "sendToChat": false,
  "flightWhitelist": [
    "ExamplePlayerName"
  ],
  "messages": {
    "prefix": "§6§lN.E.D.C §r§7| §r",
    "glideCancel": "§cElytras are disabled during combat.",
    "reload": "§aAll configs were reloaded!",
    "save": "§aAll configs were saved!"
  }
}
```

---

## Building

Requires **Java 21**.

```bash
./gradlew build
```

Built mod jars will be located in `build/libs/`:
- `no-elytra-during-combat-fabric-1.0.0.jar`

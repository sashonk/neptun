# Neptun

Grid dungeon game built with [libGDX](https://libgdx.com/) 1.11. Dig dirt, push stones, dodge golems, pick up the key, reach the door.

Requires **Java 8** and **Maven**.

## Run (desktop)

```bash
mvn package -Pdesktop
java -jar desktop/target/neptun-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or run `ru.asocial.games.java.NeptunDesktop` from the IDE.

With no arguments the desktop launcher defaults to the **300×300** map.

### Launch arguments

| Arg | Effect |
|-----|--------|
| `map40` | Fixed 40×40 dungeon (`assets/dungeons/40.txt`) |
| `map150` | Fixed 150×150 dungeon |
| `map300` | Fixed 300×300 dungeon (desktop default) |
| `metrics` | FPS / draw overlay |
| `debug` | Extra entity-matrix debug |
| `replay` | Replay recorded moves instead of live input |

Example:

```bash
java -jar desktop/target/neptun-desktop-1.0-SNAPSHOT-jar-with-dependencies.jar map40 metrics
```

## Controls

| Input | Action |
|-------|--------|
| **W A S D** / D-pad | Move |
| **B** / bomb button | Place bomb |
| **Space** (hold) | Dig or interact without stepping into the cell |

HUD: D-pad bottom-left, bomb bottom-right (3 bombs per level, 3 s cooldown).

## Gameplay

1. Splash → menu → **Play**.
2. Find the **key**, then walk into the **exit** door. The door does nothing until you have the key.
3. Death (golem, explosion, falling stone) restarts the **same seed** and restores 3 bombs.
4. Exit with the key → “level complete” → next level (new seed).

Stones fall and can be pushed sideways. Some stones explode. Golems wander and grab you on contact.

## Modules

Always-built:

- `core` — game logic, screens, entities
- `packer` — texture packing / dungeon CSV tools
- `dungeonmaker` — JNI wrapper around native `dmaker` (procedural maps)

Profiles:

```bash
mvn package -Pdesktop
mvn package -Pandroid
```

`html` and `ios` profiles exist but are not the usual workflow.

Shared assets live in `assets/` (copied into the desktop jar).

## Android

```bash
mvn package -Pandroid
```

Touch HUD is the main control scheme. Signing uses `android/game.keystore` (passwords via `-Dkeystore.password=...`).

## Docs

- [`TODO.txt`](TODO.txt) — current tasks
- [`docs/IDEAS.md`](docs/IDEAS.md) — longer-term ideas and level goals

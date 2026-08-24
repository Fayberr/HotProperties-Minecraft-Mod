# HotProperties

A server-side Fabric mod that changes `server.properties` values while the server is
running, without a restart. Changes are written back to the file, so they survive
restarts.

## Commands

All commands require operator level 4 (owner permission). `/hotproperties` is an alias
for `/hp`.

| Command | Effect |
| --- | --- |
| `/hp reload` | Reload `server.properties` and apply changes. Dedicated servers only. |
| `/hp view-distance <2-32>` | Set view distance. |
| `/hp simulation-distance <2-32>` | Set simulation distance. |
| `/hp motd <text>` | Set the server MOTD. |
| `/hp idle-timeout <minutes>` | Set the idle kick timeout. `0` disables it. |
| `/hp spawn-protection <radius>` | Set spawn protection radius. `0` disables it. Dedicated servers only. |
| `/hp command-blocks <true/false>` | Enable or disable command blocks. Dedicated servers only. |

Everything is applied through native server APIs: view distance and simulation distance
cascade to players and chunk sources, spawn protection uses the dedicated server setter,
and command blocks are toggled via the `command_blocks_work` game rule. No mixins are
used.

## Building

JDK 25 and a Fabric 26.1.2 development environment.

```bash
./gradlew build
```

The jar is in `build/libs/`.

## License

GPL-3.0-or-later

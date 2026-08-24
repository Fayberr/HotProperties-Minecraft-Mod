# HotProperties

A server-side Fabric mod that changes `server.properties` values while the server is
running, without a restart. Changes are written back to the file, so they survive
restarts.

## Commands

All commands require operator level 4. `/hotproperties` is an alias for `/hp`.

| Command | Effect |
| --- | --- |
| `/hp reload` | Reload `server.properties` and apply changes. Dedicated servers only. |
| `/hp view-distance <2-32>` | Set view distance. |
| `/hp simulation-distance <2-32>` | Set simulation distance. |
| `/hp motd <text>` | Set the server MOTD. |
| `/hp idle-timeout <minutes>` | Set the idle kick timeout. `0` disables it. |
| `/hp spawn-protection <radius>` | Set spawn protection radius. `0` disables it. Dedicated servers only. |
| `/hp command-blocks <true/false>` | Enable or disable command blocks. Dedicated servers only. |

View distance, simulation distance, MOTD and idle timeout apply immediately through
the server API. Spawn protection and command blocks take effect through mixins.

## Building

JDK 21 and a Fabric 1.21.10 development environment.

```bash
./gradlew build
```

The jar is in `build/libs/`.

## License

GPL-3.0-or-later

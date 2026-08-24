package com.hotproperties;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesManager {

    private static final Path PROPERTIES_PATH = Paths.get("server.properties");

    public static void setProperty(String key, String value) {
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(PROPERTIES_PATH);
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.startsWith(key + "=")) {
                    lines.set(i, key + "=" + value);
                    found = true;
                    break;
                }
            }
            if (!found) {
                lines.add(key + "=" + value);
            }
            java.nio.file.Files.write(PROPERTIES_PATH, lines);
        } catch (IOException e) {
            HotProperties.LOGGER.error("Failed to update server.properties", e);
        }
    }

    public static void reloadFromProperties(DedicatedServer server) {
        HotProperties.LOGGER.info("Checking server.properties for changes...");
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_PATH.toFile())) {
            properties.load(in);
        } catch (IOException e) {
            HotProperties.LOGGER.error("Failed to load server.properties for reload", e);
            return;
        }

        // View Distance
        if (properties.containsKey("view-distance")) {
            int val = Integer.parseInt(properties.getProperty("view-distance"));
            if (server.getPlayerList().getViewDistance() != val) {
                HotProperties.LOGGER.info("Detected changed value for view-distance to {}", val);
                server.setViewDistance(val);
            }
        }

        // Simulation Distance
        if (properties.containsKey("simulation-distance")) {
            int val = Integer.parseInt(properties.getProperty("simulation-distance"));
            if (server.getPlayerList().getSimulationDistance() != val) {
                HotProperties.LOGGER.info("Detected changed value for simulation-distance to {}", val);
                server.setSimulationDistance(val);
            }
        }

        // MOTD
        if (properties.containsKey("motd")) {
            String val = properties.getProperty("motd");
            if (!val.equals(server.getMotd())) {
                HotProperties.LOGGER.info("Detected changed value for motd to {}", val);
                server.setMotd(val);
            }
        }

        // Idle Timeout
        if (properties.containsKey("player-idle-timeout")) {
            int val = Integer.parseInt(properties.getProperty("player-idle-timeout"));
            if (server.playerIdleTimeout() != val) {
                HotProperties.LOGGER.info("Detected changed value for player-idle-timeout to {}", val);
                server.setPlayerIdleTimeout(val);
            }
        }

        // Spawn Protection
        if (properties.containsKey("spawn-protection")) {
            int val = Integer.parseInt(properties.getProperty("spawn-protection"));
            if (server.spawnProtectionRadius() != val) {
                HotProperties.LOGGER.info("Detected changed value for spawn-protection to {}", val);
                server.setSpawnProtectionRadius(val);
            }
        }

        // Command Blocks (game rule in 26.1.x, applied per level)
        if (properties.containsKey("enable-command-block")) {
            boolean val = Boolean.parseBoolean(properties.getProperty("enable-command-block"));
            boolean changed = false;
            for (ServerLevel level : server.getAllLevels()) {
                if (level.isCommandBlockEnabled() != val) {
                    level.getGameRules().set(GameRules.COMMAND_BLOCKS_WORK, val, server);
                    changed = true;
                }
            }
            if (changed) {
                HotProperties.LOGGER.info("Detected changed value for enable-command-block to {}", val);
            }
        }

        HotProperties.LOGGER.info("Finished checking server.properties.");
    }
}

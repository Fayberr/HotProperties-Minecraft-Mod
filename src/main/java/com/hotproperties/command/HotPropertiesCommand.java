package com.hotproperties.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class HotPropertiesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("hp")
            .requires(source -> source.hasPermissionLevel(4))
            .then(CommandManager.literal("reload")
                .executes(context -> executeReload(context.getSource())))
            .then(CommandManager.literal("view-distance")
                .then(CommandManager.argument("distance", IntegerArgumentType.integer(2, 32))
                    .executes(context -> executeViewDistance(context.getSource(), IntegerArgumentType.getInteger(context, "distance")))))
            .then(CommandManager.literal("simulation-distance")
                .then(CommandManager.argument("distance", IntegerArgumentType.integer(2, 32))
                    .executes(context -> executeSimulationDistance(context.getSource(), IntegerArgumentType.getInteger(context, "distance")))))
            .then(CommandManager.literal("motd")
                .then(CommandManager.argument("motd", StringArgumentType.greedyString())
                    .executes(context -> executeMotd(context.getSource(), StringArgumentType.getString(context, "motd")))))
            .then(CommandManager.literal("idle-timeout")
                .then(CommandManager.argument("timeout", IntegerArgumentType.integer(0))
                    .executes(context -> executeIdleTimeout(context.getSource(), IntegerArgumentType.getInteger(context, "timeout")))))
            .then(CommandManager.literal("spawn-protection")
                .then(CommandManager.argument("radius", IntegerArgumentType.integer(0))
                    .executes(context -> executeSpawnProtection(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
            .then(CommandManager.literal("command-blocks")
                .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                    .executes(context -> executeCommandBlocks(context.getSource(), BoolArgumentType.getBool(context, "enabled")))))
        );

        // Alias /hotproperties
        dispatcher.register(CommandManager.literal("hotproperties")
            .requires(source -> source.hasPermissionLevel(4))
            .redirect(dispatcher.getRoot().getChild("hp")));
    }

    private static int executeReload(ServerCommandSource source) {
        if (source.getServer() instanceof net.minecraft.server.dedicated.MinecraftDedicatedServer dedicatedServer) {
            com.hotproperties.PropertiesManager.reloadFromProperties(dedicatedServer);
            source.sendFeedback(() -> Text.literal("Reloaded server.properties!"), true);
        } else {
            source.sendError(Text.literal("Cannot reload properties on a non-dedicated server."));
        }
        return 1;
    }

    private static int executeViewDistance(ServerCommandSource source, int distance) {
        com.hotproperties.HotProperties.LOGGER.info("Changing view-distance to {}", distance);
        
        // Update the server's global value so new worlds/players get it
        source.getServer().getPlayerManager().setViewDistance(distance);
        
        // Also update all existing worlds and send packets to all online players
        for (net.minecraft.server.network.ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket(distance));
        }

        // Try to update the ChunkManager for each world
        for (net.minecraft.server.world.ServerWorld world : source.getServer().getWorlds()) {
            world.getChunkManager().applyViewDistance(distance);
        }

        com.hotproperties.PropertiesManager.setProperty("view-distance", String.valueOf(distance));
        source.sendFeedback(() -> Text.literal("Set view-distance to " + distance), true);
        return 1;
    }

    private static int executeSimulationDistance(ServerCommandSource source, int distance) {
        com.hotproperties.HotProperties.LOGGER.info("Changing simulation-distance to {}", distance);
        source.getServer().getPlayerManager().setSimulationDistance(distance);

        for (net.minecraft.server.network.ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket(distance));
        }
        
        for (net.minecraft.server.world.ServerWorld world : source.getServer().getWorlds()) {
            world.getChunkManager().applySimulationDistance(distance);
        }

        com.hotproperties.PropertiesManager.setProperty("simulation-distance", String.valueOf(distance));
        source.sendFeedback(() -> Text.literal("Set simulation-distance to " + distance), true);
        return 1;
    }

    private static int executeMotd(ServerCommandSource source, String motd) {
        com.hotproperties.HotProperties.LOGGER.info("Changing motd to {}", motd);
        source.getServer().setMotd(motd);
        com.hotproperties.PropertiesManager.setProperty("motd", motd);
        source.sendFeedback(() -> Text.literal("Set MOTD to: " + motd), true);
        return 1;
    }

    private static int executeIdleTimeout(ServerCommandSource source, int timeout) {
        com.hotproperties.HotProperties.LOGGER.info("Changing player-idle-timeout to {}", timeout);
        source.getServer().setPlayerIdleTimeout(timeout);
        com.hotproperties.PropertiesManager.setProperty("player-idle-timeout", String.valueOf(timeout));
        source.sendFeedback(() -> Text.literal("Set idle-timeout to " + timeout + " minutes"), true);
        return 1;
    }

    private static int executeSpawnProtection(ServerCommandSource source, int radius) {
        if (source.getServer() instanceof net.minecraft.server.dedicated.MinecraftDedicatedServer dedicatedServer) {
            com.hotproperties.HotProperties.LOGGER.info("Changing spawn-protection to {}", radius);
            com.hotproperties.PropertiesManager.setLiveSpawnProtection(radius);
            com.hotproperties.PropertiesManager.setProperty("spawn-protection", String.valueOf(radius));
            source.sendFeedback(() -> Text.literal("Saved spawn-protection to " + radius), true);
        }
        return 1;
    }

    private static int executeCommandBlocks(ServerCommandSource source, boolean enabled) {
        if (source.getServer() instanceof net.minecraft.server.dedicated.MinecraftDedicatedServer dedicatedServer) {
            com.hotproperties.HotProperties.LOGGER.info("Changing enable-command-block to {}", enabled);
            com.hotproperties.PropertiesManager.setLiveCommandBlocksEnabled(enabled);
            com.hotproperties.PropertiesManager.setProperty("enable-command-block", String.valueOf(enabled));
            source.sendFeedback(() -> Text.literal("Saved command-blocks enabled to " + enabled), true);
        }
        return 1;
    }
}

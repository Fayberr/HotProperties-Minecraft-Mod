package com.hotproperties.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.hotproperties.HotProperties;
import com.hotproperties.PropertiesManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.gamerules.GameRules;

public class HotPropertiesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hp")
            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER))
            .then(Commands.literal("reload")
                .executes(context -> executeReload(context.getSource())))
            .then(Commands.literal("view-distance")
                .then(Commands.argument("distance", IntegerArgumentType.integer(2, 32))
                    .executes(context -> executeViewDistance(context.getSource(), IntegerArgumentType.getInteger(context, "distance")))))
            .then(Commands.literal("simulation-distance")
                .then(Commands.argument("distance", IntegerArgumentType.integer(2, 32))
                    .executes(context -> executeSimulationDistance(context.getSource(), IntegerArgumentType.getInteger(context, "distance")))))
            .then(Commands.literal("motd")
                .then(Commands.argument("motd", StringArgumentType.greedyString())
                    .executes(context -> executeMotd(context.getSource(), StringArgumentType.getString(context, "motd")))))
            .then(Commands.literal("idle-timeout")
                .then(Commands.argument("timeout", IntegerArgumentType.integer(0))
                    .executes(context -> executeIdleTimeout(context.getSource(), IntegerArgumentType.getInteger(context, "timeout")))))
            .then(Commands.literal("spawn-protection")
                .then(Commands.argument("radius", IntegerArgumentType.integer(0))
                    .executes(context -> executeSpawnProtection(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
            .then(Commands.literal("command-blocks")
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                    .executes(context -> executeCommandBlocks(context.getSource(), BoolArgumentType.getBool(context, "enabled")))))
        );

        // Alias /hotproperties
        dispatcher.register(Commands.literal("hotproperties")
            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER))
            .redirect(dispatcher.getRoot().getChild("hp")));
    }

    private static int executeReload(CommandSourceStack source) {
        if (source.getServer() instanceof DedicatedServer dedicatedServer) {
            PropertiesManager.reloadFromProperties(dedicatedServer);
            source.sendSuccess(() -> Component.literal("Reloaded server.properties!"), true);
        } else {
            source.sendFailure(Component.literal("Cannot reload properties on a non-dedicated server."));
        }
        return 1;
    }

    private static int executeViewDistance(CommandSourceStack source, int distance) {
        if (source.getServer() instanceof DedicatedServer dedicatedServer) {
            HotProperties.LOGGER.info("Changing view-distance to {}", distance);
            dedicatedServer.setViewDistance(distance);
            PropertiesManager.setProperty("view-distance", String.valueOf(distance));
            source.sendSuccess(() -> Component.literal("Set view-distance to " + distance), true);
        } else {
            source.sendFailure(Component.literal("This command can only be run on a dedicated server."));
        }
        return 1;
    }

    private static int executeSimulationDistance(CommandSourceStack source, int distance) {
        if (source.getServer() instanceof DedicatedServer dedicatedServer) {
            HotProperties.LOGGER.info("Changing simulation-distance to {}", distance);
            dedicatedServer.setSimulationDistance(distance);
            PropertiesManager.setProperty("simulation-distance", String.valueOf(distance));
            source.sendSuccess(() -> Component.literal("Set simulation-distance to " + distance), true);
        } else {
            source.sendFailure(Component.literal("This command can only be run on a dedicated server."));
        }
        return 1;
    }

    private static int executeMotd(CommandSourceStack source, String motd) {
        HotProperties.LOGGER.info("Changing motd to {}", motd);
        MinecraftServer server = source.getServer();
        server.setMotd(motd);
        PropertiesManager.setProperty("motd", motd);
        source.sendSuccess(() -> Component.literal("Set MOTD to: " + motd), true);
        return 1;
    }

    private static int executeIdleTimeout(CommandSourceStack source, int timeout) {
        HotProperties.LOGGER.info("Changing player-idle-timeout to {}", timeout);
        MinecraftServer server = source.getServer();
        server.setPlayerIdleTimeout(timeout);
        PropertiesManager.setProperty("player-idle-timeout", String.valueOf(timeout));
        source.sendSuccess(() -> Component.literal("Set idle-timeout to " + timeout + " minutes"), true);
        return 1;
    }

    private static int executeSpawnProtection(CommandSourceStack source, int radius) {
        if (source.getServer() instanceof DedicatedServer dedicatedServer) {
            HotProperties.LOGGER.info("Changing spawn-protection to {}", radius);
            dedicatedServer.setSpawnProtectionRadius(radius);
            PropertiesManager.setProperty("spawn-protection", String.valueOf(radius));
            source.sendSuccess(() -> Component.literal("Saved spawn-protection to " + radius), true);
        } else {
            source.sendFailure(Component.literal("This command can only be run on a dedicated server."));
        }
        return 1;
    }

    private static int executeCommandBlocks(CommandSourceStack source, boolean enabled) {
        if (source.getServer() instanceof DedicatedServer dedicatedServer) {
            HotProperties.LOGGER.info("Changing enable-command-block to {}", enabled);
            MinecraftServer server = source.getServer();
            for (ServerLevel level : server.getAllLevels()) {
                level.getGameRules().set(GameRules.COMMAND_BLOCKS_WORK, enabled, server);
            }
            PropertiesManager.setProperty("enable-command-block", String.valueOf(enabled));
            source.sendSuccess(() -> Component.literal("Saved command-blocks enabled to " + enabled), true);
        } else {
            source.sendFailure(Component.literal("This command can only be run on a dedicated server."));
        }
        return 1;
    }
}

package com.hotproperties;

import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerChunkManager;

public class TestLiveUpdate {
    public static void test(ServerWorld world, ServerPlayerEntity player, int distance) {
        player.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(distance));
        player.networkHandler.sendPacket(new SimulationDistanceS2CPacket(distance));
        ServerChunkManager scm = world.getChunkManager();
        // scm.applyViewDistance(distance); // Does this exist?
    }
}

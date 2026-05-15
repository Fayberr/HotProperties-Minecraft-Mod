package com.hotproperties;

import net.minecraft.server.world.ServerChunkManager;

public class Test2 {
    public static void test(ServerChunkManager scm) {
        scm.applyViewDistance(10);
        scm.applySimulationDistance(10);
    }
}

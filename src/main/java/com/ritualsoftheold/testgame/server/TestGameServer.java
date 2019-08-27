package com.ritualsoftheold.testgame.server;

import com.ritualsoftheold.terra.server.manager.world.OffheapWorld;

public class TestGameServer {
    private OffheapWorld world;
    public TestGameServer() {
        //Has to be devidable by 16
        world = new OffheapWorld(gen, reg, 80, listener);
        new Thread(() -> world.initialChunkGeneration(player, node)).start();
    }
}

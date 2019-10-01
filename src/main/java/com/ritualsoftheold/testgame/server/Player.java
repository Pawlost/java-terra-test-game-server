package com.ritualsoftheold.testgame.server;

import com.ritualsoftheold.terra.core.chunk.ChunkLArray;
import com.ritualsoftheold.terra.server.LoadMarker;

public class Player extends LoadMarker {
    private ClientSender sender;

    Player(float posX, float posY, float posZ, int radius, ClientSender sender) {
        super(posX, posY, posZ, radius, radius);
        this.sender = sender;
    }

    @Override
    public void sendChunk(ChunkLArray chunk) {
        sender.chunkLoaded(chunk);
    }

    @Override
    protected boolean init(int id) {
        return false;
    }

    @Override
    protected void sendPosition(float x, float y, float z) {

    }

    @Override
    public void move(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

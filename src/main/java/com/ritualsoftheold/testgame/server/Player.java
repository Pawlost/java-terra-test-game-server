package com.ritualsoftheold.testgame.server;

import com.ritualsoftheold.terra.core.chunk.ChunkLArray;
import com.ritualsoftheold.terra.server.LoadMarker;

public class Player extends LoadMarker {
    private ClientSender sender;

    public Player(ClientSender sender) {
        super(0.0f, 0.0f, 0.0f, 16, 16);
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

    }
}

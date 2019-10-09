package com.ritualsoftheold.testgame.server;

import com.ritualsoftheold.terra.core.WorldLoadListener;
import com.ritualsoftheold.terra.core.chunk.ChunkLArray;
import com.ritualsoftheold.terra.core.octrees.OffheapOctree;
import com.ritualsoftheold.testgame.client.network.Client;

import java.util.ArrayList;

public class ClientSender implements WorldLoadListener {
    private ArrayList<Client> clients;

    ClientSender(ArrayList<Client> clients) {
        this.clients = clients;
    }

    @Override
    public void chunkLoaded(ChunkLArray chunk) {
        for (Client client : clients) {
            client.sendChunk(chunk);
        }
    }

    @Override
    public void chunkUnloaded(ChunkLArray chunk) {

    }

    @Override
    public void octreeLoaded(OffheapOctree octree) {
        for (Client client : clients) {
            client.sendOctree(octree);
        }
    }
}

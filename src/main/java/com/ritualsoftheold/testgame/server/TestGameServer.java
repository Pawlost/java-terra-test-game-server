package com.ritualsoftheold.testgame.server;

import com.ritualsoftheold.terra.core.materials.Registry;
import com.ritualsoftheold.terra.core.materials.TerraModule;
import com.ritualsoftheold.terra.core.octrees.OctreeBase;
import com.ritualsoftheold.terra.manager.WorldGeneratorInterface;
import com.ritualsoftheold.terra.manager.world.OffheapLoadMarker;
import com.ritualsoftheold.terra.manager.world.OffheapWorld;
import com.ritualsoftheold.testgame.client.TestGameClient;
import com.ritualsoftheold.foreman.main.generation.WeltschmerzWorldGenerator;
import com.ritualsoftheold.foreman.main.materials.PrimitiveResourcePack;
import com.ritualsoftheold.testgame.client.network.Client;
import com.ritualsoftheold.testgame.client.network.Server;

import java.util.ArrayList;

public class TestGameServer implements Server {
    private OffheapWorld world;
    private OffheapLoadMarker player;
    private ArrayList<Client> clients;
    private ArrayList<OctreeBase> node;
    private ClientSender sender;
    private Registry registry;

    public static void main(String[] args){
        TestGameServer server = new TestGameServer();
        TestGameClient client = new TestGameClient(server, server.getRegistry());
    }

    private TestGameServer() {
        //Has to be devidable by 16
        clients = new ArrayList<>();
        TerraModule mod = new TerraModule("testgame");
        registry = new Registry();
        PrimitiveResourcePack resourcePack = new PrimitiveResourcePack(registry);
        resourcePack.registerObjects(mod);
        WorldGeneratorInterface gen = new WeltschmerzWorldGenerator().setup(registry, mod);
        node = new ArrayList<>(10000000);
        sender = new ClientSender(clients);
        world = new OffheapWorld(gen, registry, 80, sender);
    }

    public Registry getRegistry(){
        return registry;
    }

    @Override
    public ArrayList<OctreeBase> init(Client client){
        player = world.createLoadMarker(client.getPosX(), client.getPosY(), client.getPosZ(),
                16, 16, 0);
        new Thread(() -> world.initialChunkGeneration(player, node)).start();
        clients.add(client);
        return node;
    }

    @Override
    public void update() {
        /*int camX = (int) (cam.getLocation().x / 16f) * 16;
        int playerX = (int) (player.getX() / 16f) * 16;
        int camZ = (int) (cam.getLocation().z / 16f) * 16;
        int playerZ = (int) (player.getZ() / 16f) * 16;

        if (geomCreateQueue.isEmpty() && !player.hasMoved() && geomDeleteQueue.isEmpty()) {
            if (camX != playerX || camZ != playerZ) {

                if (camX > playerX) {
                    playerX += 16;
                } else if (camX < playerX) {
                    playerX -= 16;
                }

                if (camZ > playerZ) {
                    playerZ += 16;
                } else if (camZ < playerZ) {
                    playerZ -= 16;
                }

                player.move(playerX, (int) cam.getLocation().y, playerZ);
                //  new Thread(() -> world.updateLoadMarker(player, false)).start();
            }
        }*/
    }
}

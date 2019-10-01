package com.ritualsoftheold.testgame.server;

import com.jme3.app.LegacyApplication;
import com.jme3.asset.AssetManager;
import com.jme3.system.JmeSystem;
import com.ritualsoftheold.foreman.CGHandler;
import com.ritualsoftheold.loader.config.PrimitiveResourcePack;
import com.ritualsoftheold.terra.core.materials.Registry;
import com.ritualsoftheold.terra.core.materials.TerraModule;
import com.ritualsoftheold.terra.server.LoadMarker;
import com.ritualsoftheold.terra.server.chunks.ChunkGenerator;
import com.ritualsoftheold.terra.server.world.ServerWorld;
import com.ritualsoftheold.testgame.client.TestGameClient;
import com.ritualsoftheold.testgame.client.network.Client;
import com.ritualsoftheold.testgame.client.network.Server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestGameServer extends LegacyApplication implements Server {
    private ServerWorld world;
    private LoadMarker player;
    private ArrayList<Client> clients;
    private static final Logger logger = Logger.getLogger(LegacyApplication.class.getName());

    public static void main(String[] args) {
        TestGameServer server = new TestGameServer();
        new TestGameClient(server);
    }

    private TestGameServer() {
        //Has to be devidable by 16
        initAssetManager();
        clients = new ArrayList<>();
        TerraModule mod = new TerraModule("testgame");
        Registry registry = new Registry();
        PrimitiveResourcePack resourcePack = new PrimitiveResourcePack(assetManager);
        resourcePack.registerObjects(mod);
        mod.registerMaterials(registry);
        CGHandler handler = new CGHandler();
        ChunkGenerator cg = handler.getGenerator();
        cg.setMaterials(mod, registry);
        ClientSender sender = new ClientSender(clients);
        player = new Player(0.0f, 0.0f, 0.0f, 16, sender);
        world = new ServerWorld(16, 0, 16, cg, registry, 100);
    }

    @Override
    public void init(Client client) {
        new Thread(() -> world.initialWorldGeneration(player)).start();
        clients.add(client);
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

    private void initAssetManager() {
        URL assetCfgUrl = null;
        if (settings != null) {
            String assetCfg = settings.getString("AssetConfigURL");
            if (assetCfg != null) {
                try {
                    assetCfgUrl = new URL(assetCfg);
                } catch (MalformedURLException ignored) {
                }
                if (assetCfgUrl == null) {
                    assetCfgUrl = LegacyApplication.class.getClassLoader().getResource(assetCfg);
                    if (assetCfgUrl == null) {
                        logger.log(Level.SEVERE, "Unable to access AssetConfigURL in asset config:{0}", assetCfg);
                        return;
                    }
                }
            }
        }
        if (assetCfgUrl == null) {
            assetCfgUrl = JmeSystem.getPlatformAssetConfigURL();
        }
        if (assetManager == null) {
            assetManager = JmeSystem.newAssetManager(assetCfgUrl);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}

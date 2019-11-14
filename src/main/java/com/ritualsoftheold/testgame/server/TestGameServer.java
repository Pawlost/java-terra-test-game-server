package com.ritualsoftheold.testgame.server;

import com.jme3.app.LegacyApplication;
import com.jme3.asset.AssetManager;
import com.jme3.system.JmeSystem;
import com.ritualsoftheold.foreman.CGHandler;
import com.ritualsoftheold.loader.config.PrimitiveResourcePack;
import com.ritualsoftheold.terra.core.materials.Registry;
import com.ritualsoftheold.terra.core.materials.TerraModule;
import com.ritualsoftheold.terra.server.LoadMarker;
import com.ritualsoftheold.terra.server.world.ChunkGenerator;
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
    private static final int MAX_WORLD_SIZE = 2097151;
    private static final long WORLD_SIZE = 4000;
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
        player = new Player(0.0f, 0.0f, 0.0f, 3, sender);
        for (int i : player.getPlayerOctants()) {
            System.out.println(i);
        }
        world = new ServerWorld(0, 0, 0, cg, registry, (int) WORLD_SIZE);
    }

    @Override
    public void init(Client client) {
        new Thread(() -> world.initialWorldGeneration(player)).start();
        clients.add(client);
        client.sendOctree(world.octree);
    }

    @Override
    public void update() {
        player.move(cam.getLocation().x, cam.getLocation().y, cam.getLocation().y);
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

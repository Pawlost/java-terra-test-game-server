package com.ritualsoftheold.terra.net.test;

import java.util.concurrent.ForkJoinPool;

import com.ritualsoftheold.terra.net.server.SendingLoadListener;
import com.ritualsoftheold.terra.net.server.WorldObserver;
import com.ritualsoftheold.terra.core.TerraModule;
import com.ritualsoftheold.terra.manager.material.MaterialRegistry;
import com.ritualsoftheold.terra.manager.memory.MemoryPanicHandler;
import com.ritualsoftheold.terra.net.TerraProtocol;
import com.ritualsoftheold.terra.memory.chunk.ChunkBuffer;
import com.ritualsoftheold.terra.manager.io.dummy.DummyChunkLoader;
import com.ritualsoftheold.terra.manager.io.dummy.DummyOctreeLoader;
import com.ritualsoftheold.terra.manager.world.OffheapWorld;
import com.ritualsoftheold.terra.manager.world.LoadMarker;
import com.ritualsoftheold.terra.manager.world.gen.WorldGeneratorInterface;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.driver.MediaDriver;

public class TerraServerTest {
    
    private OffheapWorld world;
    
    public void launch() {
        MediaDriver.launch();
        
        ChunkBuffer.Builder bufferBuilder = new ChunkBuffer.Builder()
                .maxChunks(128)
                .queueSize(4);
        
        TerraModule mod = new TerraModule("testgame");
        mod.newMaterial().name("dirt");
        mod.newMaterial().name("grass");
        MaterialRegistry reg = new MaterialRegistry();
        mod.registerMaterials(reg);
        
        WorldGeneratorInterface<?> gen = new TestWorldGeneratorInterface();
        gen.setup(0, reg);
        
        world = new OffheapWorld.Builder()
                .chunkLoader(new DummyChunkLoader())
                .octreeLoader(new DummyOctreeLoader(32768))
                .storageExecutor(ForkJoinPool.commonPool())
                .chunkStorage(bufferBuilder, 128)
                .octreeStorage(32768)
                .generator(gen)
                .generatorExecutor(ForkJoinPool.commonPool())
                .materialRegistry(reg) // TODO server sends this
                .memorySettings(10000000, 10000000, new MemoryPanicHandler() {
                    
                    @Override
                    public PanicResult outOfMemory(long max, long used, long possible) {
                        return PanicResult.CONTINUE;
                    }
                    
                    @Override
                    public PanicResult goalNotMet(long goal, long possible) {
                        return PanicResult.CONTINUE;
                    }
                })
                .build();
        
        SendingLoadListener listener = new SendingLoadListener(world);
        world.setLoadListener(listener);
        
        LoadMarker marker = world.createLoadMarker(0, 10, 0, 32, 32, 0);
        world.addLoadMarker(marker);
        
        Aeron aeron = Aeron.connect(new Aeron.Context());
        Publication publication = aeron.addPublication("aeron:udp?endpoint=localhost:40123", TerraProtocol.DEFAULT_AERON_STREAM);
        WorldObserver observer = new WorldObserver(marker, publication, (byte) 10);
        listener.addObserver(observer);
    }
    
    public static void main(String... args) {
        new TerraServerTest().launch();
    }
}

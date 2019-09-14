package com.ritualsoftheold.testgame.server.generation;

import com.ritualsoftheold.terra.core.chunk.ChunkLArray;
import com.ritualsoftheold.terra.core.materials.Registry;
import com.ritualsoftheold.terra.core.materials.TerraModule;
import com.ritualsoftheold.terra.core.materials.TerraObject;
import com.ritualsoftheold.foreman.main.Foreman;
import com.ritualsoftheold.terra.server.manager.gen.interfaces.WorldGeneratorInterface;

public class WeltschmerzWorldGenerator implements WorldGeneratorInterface {

    private Foreman foreman;

    @Override
    public  WorldGeneratorInterface setup(Registry reg, TerraModule mod) {
        foreman = new Foreman();
        foreman.setMaterials(
                reg.getMaterial(mod,"dirt").getWorldId(),
                reg.getMaterial(mod, "grass").getWorldId(),
                reg.getMaterial(mod, "Tall_Grass-mesh_variant01-01").getWorldId());

        TerraObject tree =  reg.getMaterial(mod, "birch-02_baked");
        foreman.setObject(tree.getMesh().getDefaultDistanceX(), tree.getMesh().getDefaultDistanceY(),
                tree.getMesh().getDefaultDistanceZ(), tree.getMesh().getId());
        return this;
    }

    public void generate(ChunkLArray chunk) {
        foreman.getChunk((int)chunk.x, (int)chunk.y, (int)chunk.z, chunk.getChunkVoxelData());
        chunk.setDifferent(foreman.isDifferent());
    }
}
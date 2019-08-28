package com.ritualsoftheold.foreman.main.materials;

import com.jme3.app.LegacyApplication;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeSystem;
import com.ritualsoftheold.loader.BlockMaker;
import com.ritualsoftheold.loader.ModelLoader3D;
import com.ritualsoftheold.terra.core.materials.Registry;
import com.ritualsoftheold.terra.core.materials.TerraMesh;
import com.ritualsoftheold.terra.core.materials.TerraModule;
import com.ritualsoftheold.terra.core.materials.TerraTexture;
import java.util.logging.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class PrimitiveResourcePack extends LegacyApplication {
    private Registry reg;
    private static final Logger logger = Logger.getLogger(LegacyApplication.class.getName());


    public PrimitiveResourcePack(Registry reg){
        super();
        initAssetManager();
        this.reg = reg;
    }

    public void registerObjects(TerraModule mod){
        ModelLoader3D modelLoader3D = new ModelLoader3D(assetManager);

        //Textures
        mod.newMaterial().name("dirt").texture(new TerraTexture("NorthenForestDirt256px.png"));
        mod.newMaterial().name("grass").texture(new TerraTexture("NorthenForestGrass256px.png"));

        Spatial asset =  modelLoader3D.getMesh("Tall_Grass-mesh_variant01-01");
        //Custom meshes

        BlockMaker maker = new BlockMaker(asset);

        mod.newMaterial().name("Tall_Grass-mesh_variant01-01").model(new TerraMesh("Tall_Grass-mesh_variant01-01",
                maker.getDefaultDistanceX(), maker.getDefaultDistanceY(), maker.getDefaultDistanceZ()))
                .texture(new TerraTexture("Tall_Grass_textures-variant01-Diffuse-01.png", true));

        Spatial spatial = modelLoader3D.getMesh("birch-02_baked");

        maker = new BlockMaker(spatial);

        mod.newMaterial().name("birch-02_baked").model(new TerraMesh("birch-02_baked",
                maker.getDefaultDistanceX(), maker.getDefaultDistanceY(), maker.getDefaultDistanceZ()))
                .texture(new TerraTexture("birch-02_baked.png", true));

        mod.registerMaterials(reg);
    }

    private void initAssetManager(){
        URL assetCfgUrl = null;

        if (settings != null){
            String assetCfg = settings.getString("AssetConfigURL");
            if (assetCfg != null){
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
        if (assetManager == null){
            assetManager = JmeSystem.newAssetManager(assetCfgUrl);
        }
    }
}

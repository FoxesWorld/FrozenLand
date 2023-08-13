package org.foxesworld.newgame.engine.world.skybox;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

public class SkyboxGenerator {

    private final AssetManager assetManager;

    public SkyboxGenerator(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Spatial generateSkybox(String texture) {
        Spatial sky = SkyFactory.createSky(assetManager,
                texture, SkyFactory.EnvMapType.CubeMap);
        sky.setLocalScale(350);

        return sky;
    }
}
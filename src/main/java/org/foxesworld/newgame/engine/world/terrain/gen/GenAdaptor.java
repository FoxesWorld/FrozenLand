package org.foxesworld.newgame.engine.world.terrain.gen;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.config.Constants;
import org.foxesworld.newgame.engine.world.terrain.gen.mountains.MountGen;
import org.foxesworld.newgame.engine.world.terrain.gen.terrain.TerrainGen;

public class GenAdaptor implements GenAdaptorInterface {

    private final AssetManager assetManager;
    private final KernelInterface app;
    private TerrainQuad distantTerrain;

    public GenAdaptor(KernelInterface app) {
        this.assetManager = app.getAssetManager();
        this.app = app;
    }

    @Override
    public TerrainQuad generateTerrain() {
        /* TODO
        *   Add more settungs
        *   For biome customising
        *   Textures&HeightMap
        *  */
        TerrainGen terrainGen = new TerrainGen(app);
        return terrainGen.generateTerrain(0.82f,0.1f,1.1f, 2.12f, 8, 0.02125f);
    }

    @Override
    public TerrainQuad generateMountains() {
        MountGen mountGen = new MountGen(app);
        return mountGen.generateMountains();
    }

    @Override
    public void update() {
        Vector3f playerLocation = app.getPlayer().getPlayerPosition();
        playerLocation.y = Constants.MOUNTAINS_HEIGHT_OFFSET;
        distantTerrain.setLocalTranslation(playerLocation);
    }
}
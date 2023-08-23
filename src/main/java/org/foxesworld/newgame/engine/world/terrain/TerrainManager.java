package org.foxesworld.newgame.engine.world.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.terrain.geomipmap.TerrainQuad;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.world.terrain.gen.FractalTerrainGrid;

import java.util.logging.Logger;

public class TerrainManager implements TerrainManagerInterface {
    final private static Logger LOGGER = Logger.getLogger(TerrainManager.class.getName());
    private TerrainInterface terrainBuilder;

    private TerrainQuad terrain;
    private TerrainQuad mountains;

    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private KernelInterface app;

    public TerrainManager(AssetManager assetManager, BulletAppState bulletAppState, KernelInterface app) {
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.app = app;

        terrainBuilder = new FractalTerrainGrid(app);
        generateTerrain();
    }

    private void generateTerrain() {
        terrain = terrainBuilder.generateTerrain();
        mountains = terrainBuilder.generateMountains();
    }

    public void generateGrass() {

    }

    public TerrainQuad getTerrain() {
        return terrain;
    }

    @Override
    public TerrainQuad getMountains() {
        return mountains;
    }

    public void update(float tpf) {
        terrainBuilder.update();
    }
}
package org.foxesworld.frozenlands.engine.world.terrain;

import com.jme3.terrain.geomipmap.TerrainQuad;
import org.foxesworld.frozenlands.engine.Kernel;
import org.foxesworld.frozenlands.engine.world.terrain.gen.GenAdaptor;

public class TerrainManager implements TerrainManagerInterface {
    private TerrainInterface terrainBuilder;
    private Kernel kernel;
    private TerrainQuad terrain;
    private TerrainQuad mountains;

    public TerrainManager(Kernel app) {
        this.kernel = app;
        terrainBuilder = new GenAdaptor(app);
        generateTerrain();
    }

    private void generateTerrain() {
        terrain = terrainBuilder.generateTerrain();
        mountains = terrainBuilder.generateMountains();
    }

    @Override
    public void generateFoliage() {
        //IMPORTANT
    }
    @Override
    public TerrainQuad getTerrain() {
        return terrain;
    }

    @Override
    public TerrainQuad getMountains() {
        return mountains;
    }

    @Override
    public void update(float tpf) {
        generateTerrain();
    }
}
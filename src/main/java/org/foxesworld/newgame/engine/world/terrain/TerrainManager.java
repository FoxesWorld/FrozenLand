package org.foxesworld.newgame.engine.world.terrain;

import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.world.terrain.gen.FractalTerrainGrid;

public class TerrainManager implements TerrainManagerInterface {
    private TerrainInterface terrainBuilder;
    private  KernelInterface kernelInterface;
    private TerrainQuad terrain;
    private TerrainQuad mountains;

    public TerrainManager(KernelInterface app) {
        this.kernelInterface = app;
        terrainBuilder = new FractalTerrainGrid(app);
        generateTerrain();
    }

    private void generateTerrain() {
        terrain = terrainBuilder.generateTerrain();
        mountains = terrainBuilder.generateMountains();
    }

    public void generateFoliage() {
        //IMPORTANT
    }
    public TerrainQuad getTerrain() {
        return terrain;
    }

    @Override
    public TerrainQuad getMountains() {
        return mountains;
    }

    public void update(float tpf) {
        generateTerrain();
    }
}
package org.foxesworld.newgame.engine.world.terrain;

import com.jme3.terrain.geomipmap.TerrainQuad;

public interface TerrainManagerInterface {
    void generateGrass();

    TerrainQuad getMountains();

    void update(float tpf);

    TerrainQuad getTerrain();
}
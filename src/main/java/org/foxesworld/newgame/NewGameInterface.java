package org.foxesworld.newgame;

import com.jme3.app.Application;
import com.jme3.scene.Node;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.providers.sound.SoundManager;
import org.foxesworld.newgame.engine.world.terrain.TerrainManagerInterface;

public interface NewGameInterface extends Application {
    KernelInterface getGameLogicCore();

    Node getRootNode();

    SoundManager getSoundManager();

    TerrainManagerInterface getTerrainManager();

    void setTerrainManager(TerrainManagerInterface terrainManager);

    String getVersion();
}

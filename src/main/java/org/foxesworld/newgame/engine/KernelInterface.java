package org.foxesworld.newgame.engine;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.foxesworld.newgame.engine.player.Player;
import org.foxesworld.newgame.engine.providers.material.MaterialManager;
import org.foxesworld.newgame.engine.providers.sound.SoundManager;
import org.slf4j.Logger;

public interface KernelInterface {
    Node getRootNode();
    AssetManager getAssetManager();
    Player getPlayer();
    Camera getCamera();
    Logger getLogger();
    SoundManager getSoundManager();
    MaterialManager getMaterialManager();
    BulletAppState getBulletAppState();
}

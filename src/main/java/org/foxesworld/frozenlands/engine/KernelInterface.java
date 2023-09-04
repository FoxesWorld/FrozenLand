package org.foxesworld.frozenlands.engine;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.foxesworld.frozenlands.engine.player.Player;
import org.foxesworld.frozenlands.engine.providers.material.MaterialProvider;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;
import org.foxesworld.frozenlands.engine.world.sky.Sky;
import org.slf4j.Logger;

import java.util.Map;

public interface KernelInterface {
    Node getRootNode();
    Node getGuiNode();
    Sky getSky();
    ViewPort getViewPort();
    FilterPostProcessor getFpp();
    AssetManager getAssetManager();
    Map getConfig();
    AppStateManager appStateManager();
    InputManager getInputManager();
    Player getPlayer();
    Camera getCamera();
    Logger getLogger();
    SoundProvider getSoundManager();
    MaterialProvider getMaterialManager();
    BulletAppState getBulletAppState();
}

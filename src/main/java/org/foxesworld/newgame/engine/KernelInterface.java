package org.foxesworld.newgame.engine;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.foxesworld.newgame.engine.player.Player;
import org.foxesworld.newgame.engine.providers.material.MaterialProvider;
import org.foxesworld.newgame.engine.providers.sound.SoundProvider;
import org.foxesworld.newgame.engine.world.sky.Sky;
import org.slf4j.Logger;

import java.util.Map;

public interface KernelInterface {
    Node getRootNode();
    Sky getSky();
    ViewPort getViewPort();
    FilterPostProcessor getFpp();
    AssetManager getAssetManager();
    Map getCONFIG();
    AppStateManager appStateManager();
    InputManager getInputManager();
    NiftyJmeDisplay getNiftyDisplay();
    Player getPlayer();
    Camera getCamera();
    Logger getLogger();
    SoundProvider getSoundManager();
    MaterialProvider getMaterialManager();
    BulletAppState getBulletAppState();
}

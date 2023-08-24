package org.foxesworld.newgame.engine.player;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.foxesworld.newgame.engine.player.input.UserInputHandler;
import org.foxesworld.newgame.engine.providers.sound.SoundManager;

import java.util.Map;

public interface PlayerInterface {
    BetterCharacterControl getCharacterControl();
    AssetManager getAssetManager();
    SoundManager getSoundManager();
    Node getRootNode();
    Camera getFpsCam();
    AppStateManager getStateManager();
    Vector3f getPlayerPosition();
    UserInputHandler getUserInputHandler();
    Vector3f getJumpForce();
    NiftyJmeDisplay getNiftyDisplay();
    InputManager getInputManager();
    Map getCFG();
}

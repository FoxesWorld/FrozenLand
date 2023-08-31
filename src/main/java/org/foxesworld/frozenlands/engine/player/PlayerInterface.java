package org.foxesworld.frozenlands.engine.player;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.foxesworld.frozenlands.engine.player.input.UserInputHandler;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;

import java.util.Map;

public interface PlayerInterface {
    BetterCharacterControl getCharacterControl();
    AssetManager getAssetManager();
    SoundProvider getSoundManager();
    Node getRootNode();
    Node getGuiNode();
    Camera getFpsCam();
    AppStateManager getStateManager();
    Vector3f getPlayerPosition();
    UserInputHandler getUserInputHandler();
    Vector3f getJumpForce();
    InputManager getInputManager();
    Map getCFG();
}

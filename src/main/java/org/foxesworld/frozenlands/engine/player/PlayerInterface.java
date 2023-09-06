package org.foxesworld.frozenlands.engine.player;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.foxesworld.frozenlands.engine.player.input.UserInputHandler;
import org.slf4j.Logger;

import java.util.Map;

public interface PlayerInterface {
    PlayerOptions getPlayerOptions();
    PlayerModel getPlayerModel();
    AssetManager getAssetManager();
    Logger getLogger();
    PlayerSoundProvider getPlayerSoundProvider();
    Node getRootNode();
    Node getGuiNode();
    AppStateManager getStateManager();
    Vector3f getPlayerPosition();
    UserInputHandler getUserInputHandler();
    InputManager getInputManager();
    Map getConfig();
}

package org.foxesworld.frozenlands.engine.player;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.foxesworld.frozenlands.engine.player.input.UserInputHandler;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public interface PlayerInterface {
    PlayerOptions getPlayerOptions();
    PlayerModel getPlayerModel();
    AssetManager getAssetManager();
    Logger getLogger();
    SoundProvider getSoundManager();
    Map<String, List<AudioNode>> getPlayerSounds();
    Node getRootNode();
    Node getGuiNode();
    AppStateManager getStateManager();
    Vector3f getPlayerPosition();
    UserInputHandler getUserInputHandler();
    InputManager getInputManager();
    Map getConfig();
}

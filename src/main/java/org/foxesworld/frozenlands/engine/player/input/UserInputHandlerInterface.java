package org.foxesworld.frozenlands.engine.player.input;

import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;

import java.util.List;
import java.util.Map;

public interface UserInputHandlerInterface {

    void init();
    Vector3f getPlayerPosition();
}

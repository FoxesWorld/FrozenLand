package org.foxesworld.frozenlands.engine.player.input;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public abstract class UserInputAbstract extends AbstractControl implements ActionListener, AnalogListener {

    private boolean isInit = false;
    private PlayerState playerState = PlayerState.STANDING;

    private HashMap<String, List<Object>> userInputConfig;

    protected abstract void init();

    protected abstract void movePlayer(Vector3f direction, float speedMultiplier, float tpf);

    protected abstract void setPlayerState(Vector3f walkDirection, float tpf);
    @Override
    protected abstract void controlUpdate(float v);

    @Override
    protected abstract void controlRender(RenderManager renderManager, ViewPort viewPort);

    @Override
    public  abstract void onAction(String s, boolean b, float v);

    @Override
    public abstract void onAnalog(String s, float v, float v1);

    protected abstract void inputInit(Stack<String> inputMaps);

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public HashMap<String, List<Object>> getUserInputConfig() {
        return userInputConfig;
    }

    public void setUserInputConfig(HashMap<String, List<Object>> userInputConfig) {
        this.userInputConfig = userInputConfig;
    }
}

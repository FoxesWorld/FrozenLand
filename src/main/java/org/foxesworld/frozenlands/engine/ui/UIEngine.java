package org.foxesworld.frozenlands.engine.ui;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import org.foxesworld.frozenlands.FrozenLands;

public class UIEngine extends BaseAppState implements UIEngineInterface {

    private boolean arrowEnabled;
    private InputManager inputManager;
    private Node guiNode;

    public UIEngine(FrozenLands frozenLands) {
        this.inputManager = frozenLands.getInputManager();
        this.guiNode = frozenLands.getGuiNode();
    }

    @Override
    protected void initialize(Application application) {

    }

    @Override
    protected void cleanup(Application application) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tpf) {
        inputManager.setCursorVisible(false);
    }

    @Override
    public void setArrowEnabled(boolean arrowEnabled) {
        this.arrowEnabled = arrowEnabled;
    }

    @Override
    public boolean isArrowEnabled() {
        return arrowEnabled;
    }
}

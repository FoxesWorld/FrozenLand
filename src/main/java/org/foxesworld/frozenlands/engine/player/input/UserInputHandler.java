package org.foxesworld.frozenlands.engine.player.input;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.foxesworld.frozenlands.engine.player.PlayerData;
import org.foxesworld.frozenlands.engine.player.PlayerInterface;
import org.foxesworld.frozenlands.engine.player.PlayerSoundProvider;
import org.foxesworld.automaton.ComponentManager;
import org.foxesworld.frozenlands.engine.ui.UserInfo;

import java.util.*;

public class UserInputHandler extends UserInputAbstract implements UserInputHandlerInterface {

    private int playerHealth;
    private ComponentManager componentManager;
    private UserInfo userInfoBox;
    private BetterCharacterControl characterControl;
    private PlayerData playerData;
    private final InputManager inputManager;
    private final Camera cam;
    private final Node rootNode;
    private final Node guiNode;
    private AudioNode walkAudio;
    private final Runnable attackCallback;
    private boolean[] directions = new boolean[4];
    private final AssetManager assetManager;
    final float[] angles = {0, 0, 0};
    final Quaternion tmpRot = new Quaternion();
    final Vector3f tmpV3 = new Vector3f();

    public UserInputHandler(PlayerInterface player, Runnable attackCallback) {
        this.inputManager = player.getInputManager();
        this.assetManager = player.getAssetManager();
        this.rootNode = player.getRootNode();
        this.guiNode = player.getGuiNode();
        this.playerData = player.getPlayerData();
        this.attackCallback = attackCallback;
        this.cam = player.getFpsCam();
        setUserInputConfig((HashMap<String, List<Object>>) player.getCFG().get("userInput"));
        this.componentManager = new ComponentManager();
        this.userInfoBox = new UserInfo(player);
    }

    @Override
    public void init() {
        if (!isInit()) {
            characterControl = spatial.getControl(BetterCharacterControl.class);
            if (characterControl == null) {
                System.err.println(getClass() + " can be attached only to a spatial that has a BetterCharacterControl");
                return;
            }
            userInfoBox.userInfo(this.componentManager);
            inputInit(UserInputHelper.getInputMaps(getUserInputConfig()));
            setInit(true);
        }
    }

    @Override
    protected void inputInit(Stack<String> inputMaps) {
        inputMaps.forEach(inputMap -> getUserInputConfig().get(inputMap).forEach(inputLine -> {
            InputType inputType = InputType.valueOf(inputMap.toUpperCase());
            int inputKey = (Integer) ((HashMap<?, ?>) inputLine).get("inputKey");
            String inputName = (String) ((HashMap<?, ?>) inputLine).get("inputName");

            switch (inputType) {
                case KEYBOARD -> inputManager.addMapping(inputName, new KeyTrigger(inputKey));
                case MOUSEAXIS -> {
                    boolean negative = (Boolean) ((HashMap<?, ?>) inputLine).get("negative");
                    inputManager.addMapping(inputName, new MouseAxisTrigger(inputKey, negative));
                }
                case MOUSEBUTTONS -> inputManager.addMapping(inputName, new MouseButtonTrigger(inputKey));
            }

            inputManager.addListener(this, inputName);
        }));
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f walkDirection = new Vector3f(directions[0] ? 1 : directions[1] ? -1 : 0, 0, directions[2] ? 1 : directions[3] ? -1 : 0);
        movePlayer(walkDirection, 1.0f, tpf);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        float rotationMultiplier = getPlayerState().equals(PlayerState.SPRINTING) ? playerData.getRotationMultiplierWalking() : playerData.getRotationMultiplierRunning();
        value *= playerData.getCurrentSpeed() * rotationMultiplier;

        switch (name) {
            case "Rotate_Left" -> angles[1] += value;
            case "Rotate_Right" -> angles[1] -= value;
            case "Rotate_Up" -> angles[0] -= value;
            case "Rotate_Down" -> angles[0] += value;
        }

        angles[0] = FastMath.clamp(angles[0], -0.85f, 1.1f);
        tmpV3.set(Vector3f.UNIT_Z);
        tmpRot.fromAngles(angles);
        tmpRot.multLocal(tmpV3);
        characterControl.setViewDirection(tmpV3);

        Vector3f moveDirection = new Vector3f(directions[0] ? 1 : directions[1] ? -1 : 0, 0, directions[2] ? 1 : directions[3] ? -1 : 0);
        movePlayer(moveDirection, value, tpf);
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case "Left" -> directions[0] = isPressed;
            case "Right" -> directions[1] = isPressed;
            case "Up" -> directions[2] = isPressed;
            case "Down" -> directions[3] = isPressed;
            case "Attack" -> {
                playerData.setAttacking(isPressed);
                if (isPressed) {
                    attackCallback.run();
                }
            }
            case "Jump" -> {
                playerData.setJumping(isPressed);
                if (isPressed) {
                    characterControl.jump();
                    characterControl.setPhysicsDamping(0);
                }
            }
            case "Run" -> playerData.setRunning(isPressed);
        }
    }

    @Override
    protected void movePlayer(Vector3f direction, float speedMultiplier, float tpf) {
        init();
        Quaternion tmpQtr = new Quaternion();
        float targetSpeed = playerData.isRunning() ? playerData.getRunSpeed() : playerData.getWalkSpeed();
        float speedChange = targetSpeed - playerData.getCurrentSpeed();
        float actualSpeedChange = Math.signum(speedChange) * Math.min(playerData.getMaxSmoothSpeedChange() * tpf, Math.abs(speedChange));
        playerData.setCurrentSpeed(playerData.getCurrentSpeed() + actualSpeedChange);
        direction.multLocal(playerData.getCurrentSpeed() * speedMultiplier);

        tmpV3.set(characterControl.getViewDirection());
        tmpV3.y = 0;
        tmpV3.normalizeLocal();
        tmpQtr.lookAt(tmpV3, Vector3f.UNIT_Y);
        tmpQtr.multLocal(direction);

        characterControl.setWalkDirection(direction);
        characterControl.setPhysicsDamping(0.9f);
        setPlayerState(direction, tpf);

        this.componentManager.updateProgressBarValue("health", this.playerHealth);
        this.componentManager.updateLabelTexts(
                new String[]{
                        "posX",
                        "posY",
                        "posZ"
                },
                new String[]{
                        String.valueOf(this.getPlayerPosition().x),
                        String.valueOf(this.getPlayerPosition().y),
                        String.valueOf(this.getPlayerPosition().z),
                });
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Only for rendering
    }

    @Override
    protected void setPlayerState(Vector3f walkDirection, float tpf) {
        if (walkDirection.lengthSquared() == 0 && playerData.getCurrentSpeed() == playerData.getWalkSpeed()) {
            setPlayerState(PlayerState.STANDING);
        } else {
            if (walkDirection.lengthSquared() > 0) {
                if (!characterControl.isOnGround() && Math.abs(playerData.getPlayerDistanceAboveGround(spatial)) <= 1) {
                    setPlayerState(PlayerState.FLYING);
                } else {
                    if (playerData.getCurrentSpeed() > playerData.getWalkSpeed()) {
                        setPlayerState(PlayerState.SPRINTING);
                    } else {
                        setPlayerState(PlayerState.WALKING);
                    }
                }
            }
        }
    }

    public Vector3f getPlayerPosition() {
        if (this.spatial != null) {
            Vector3f worldTranslation = this.spatial.getWorldTranslation();
            float x = Math.round(worldTranslation.x);
            float y = Math.round(worldTranslation.y);
            float z = Math.round(worldTranslation.z);
            return new Vector3f(x, y, z);
        } else {
            return new Vector3f(0, 0, 0);
        }
    }
    @Override
    public void setPlayerHealth(int health) {
        this.playerHealth = health;
    }

    @Override
    public Node getRootNode() {
        return this.rootNode;
    }
}

package org.foxesworld.newgame.engine.player.input;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.style.ElementId;
import org.foxesworld.newgame.engine.player.CharacterSettings;
import org.foxesworld.newgame.engine.player.PlayerInterface;
import org.foxesworld.newgame.engine.providers.sound.SoundProvider;
import org.foxesworld.newgame.engine.ui.UserInfo;

import java.awt.*;
import java.util.List;
import java.util.*;

public class UserInputHandler extends UserInputAbstract implements UserInputHandlerI {

    private UserInfo userInfoBox;
    private BetterCharacterControl characterControl;
    protected final CharacterSettings characterSettings;
    private final InputManager inputManager;
    private final Camera cam;
    private final Node rootNode;
    private final Node guiNode;
    private AudioNode walkAudio;
    private final Runnable attackCallback;
    private boolean[] directions = new boolean[4];
    private final AssetManager assetManager;
    private final Map<String, List<AudioNode>> playerSounds;
    private  final SoundProvider soundProvider;
    final float[] angles = {0, 0, 0};
    final Quaternion tmpRot = new Quaternion();
    final Vector3f tmpV3 = new Vector3f();

    public UserInputHandler(PlayerInterface player, Runnable attackCallback) {
        this.soundProvider = player.getSoundManager();
        this.playerSounds = soundProvider.getSoundBlock("player");
        this.inputManager = player.getInputManager();
        this.assetManager = player.getAssetManager();
        this.rootNode = player.getRootNode();
        this.guiNode = player.getGuiNode();
        this.attackCallback = attackCallback;
        this.cam = player.getFpsCam();
        setUserInputConfig((HashMap<String, List<Object>>) player.getCFG().get("userInput"));
        this.characterSettings = new CharacterSettings();
        this.userInfoBox = new UserInfo(player);
    }

    @Override
    public void init() {
        if(!isInit()){
            characterControl = spatial.getControl(BetterCharacterControl.class);
            if (characterControl == null) {
                    System.err.println(getClass() + " can be attached only to a spatial that has a BetterCharacterControl");
                return;
            }
            userInfoBox.showWindow();
            inputManager.setCursorVisible(false);
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
        float rotationMultiplier = getPlayerState().equals(PlayerState.SPRINTING) ? characterSettings.getRotationMultiplierWalking() : characterSettings.getRotationMultiplierRunning();
        value *= characterSettings.getCurrentSpeed() * rotationMultiplier;

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
                characterSettings.setAttacking(isPressed);
                if (isPressed) {
                    attackCallback.run();
                }
            }
            case "Jump" -> {
                characterSettings.setJumping(isPressed);
                if (isPressed) {
                    characterControl.jump();
                    characterControl.setPhysicsDamping(0);
                }
            }
            case "Run" -> characterSettings.setRunning(isPressed);
        }
    }
    @Override
    protected void movePlayer(Vector3f direction, float speedMultiplier, float tpf) {
        init();
        Quaternion tmpQtr = new Quaternion();
        float targetSpeed = characterSettings.isRunning() ? characterSettings.getRunSpeed() : characterSettings.getWalkSpeed();
        float speedChange = targetSpeed - characterSettings.getCurrentSpeed();
        float actualSpeedChange = Math.signum(speedChange) * Math.min(characterSettings.getMaxSmoothSpeedChange() * tpf, Math.abs(speedChange));
        characterSettings.setCurrentSpeed(characterSettings.getCurrentSpeed() + actualSpeedChange);
        direction.multLocal(characterSettings.getCurrentSpeed() * speedMultiplier);

        tmpV3.set(characterControl.getViewDirection());
        tmpV3.y = 0;
        tmpV3.normalizeLocal();
        tmpQtr.lookAt(tmpV3, Vector3f.UNIT_Y);
        tmpQtr.multLocal(direction);

        characterControl.setWalkDirection(direction);
        characterControl.setPhysicsDamping(0.9f);
        setPlayerState(direction, tpf);

        updateMovementAudio(tpf);
        soundProvider.update(tpf);
        /*updateHUDText(new String[]{"speed", "playerState", "posX", "posY", "posZ"}, new String[]{
                String.valueOf(characterSettings.getCurrentSpeed()),
                String.valueOf(getPlayerState()),
                String.valueOf(this.getPlayerPosition().x),
                String.valueOf(this.getPlayerPosition().y),
                String.valueOf(this.getPlayerPosition().z)
        });
        */
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Only for rendering
    }

    @Override
    protected void setPlayerState(Vector3f walkDirection, float tpf){
        if(walkDirection.lengthSquared() == 0 && characterSettings.getCurrentSpeed() == characterSettings.getWalkSpeed()){
            setPlayerState(PlayerState.STANDING);
        } else {
            if(walkDirection.lengthSquared() > 0){
                if(!characterControl.isOnGround() && Math.abs(characterSettings.getPlayerDistanceAboveGround(spatial)) <= 1){
                    setPlayerState(PlayerState.FLYING);
                } else {
                    if (characterSettings.getCurrentSpeed() > characterSettings.getWalkSpeed()) {
                        setPlayerState(PlayerState.SPRINTING);
                    } else {
                        setPlayerState(PlayerState.WALKING);
                    }
                }
            }
        }
    }

    private void updateMovementAudio(float tpf) {
        float speed = characterSettings.getCurrentSpeed();
        float interval = Math.max(1f, speed * 0.5f) / 2;

        if (!getPlayerState().equals(PlayerState.STANDING)) {
            if ((walkAudio == null || !walkAudio.getStatus().equals(AudioSource.Status.Playing))) {
                playWalkAudio(getPlayerState().toString().toLowerCase());
            } else {
                interval -= tpf * speed; // Уменьшаем интервал на прошедшее время, умноженное на скорость
                if (interval <= 0) {
                    interval = Math.max(1f, speed * 0.5f) / 2;
                    playWalkAudio(getPlayerState().toString().toLowerCase());
                }
            }
        } else {
            stopWalkAudio();
        }
    }



    private void playWalkAudio(String userState) {
        stopWalkAudio();
        walkAudio = getRandomAudioNode(userState);
        if (walkAudio != null) {
            walkAudio.setLocalTranslation(spatial.getWorldTranslation());
            walkAudio.setPitch(characterSettings.getCurrentSpeed() / 4);
            rootNode.attachChild(walkAudio);
            walkAudio.play();
        }
    }

    private void stopWalkAudio() {
        if (walkAudio != null && walkAudio.getStatus().equals(AudioSource.Status.Playing)) {
            walkAudio.stop();
            rootNode.detachChild(walkAudio);
            walkAudio = null;
        }
    }
    /*
    private void updateHUDText(String[] elementIds, String[] values) {
        if (nifty != null) {
            Screen currentScreen = nifty.getCurrentScreen();
            if (currentScreen != null) {
                for (int i = 0; i < elementIds.length; i++) {
                    String elementId = elementIds[i];
                    String value = values[i];

                    Element hudElement = currentScreen.findElementById(elementId);
                    if (hudElement != null) {
                        TextRenderer textRenderer = hudElement.getRenderer(TextRenderer.class);
                        if (textRenderer != null) {
                            textRenderer.setText(String.valueOf(value));
                        }
                    }
                }
            }
        }
    }
    */

    public AudioNode getRandomAudioNode(String event) {
        Random random = new Random();
        List<AudioNode> audioNodes = playerSounds.get(event);
        if (audioNodes != null && !audioNodes.isEmpty()) {
            int randomIndex = random.nextInt(audioNodes.size());
            return audioNodes.get(randomIndex);
        }
        return null;
    }

    public Vector3f getPlayerPosition() {
        if (spatial != null) {
            return spatial.getWorldTranslation();
        } else {
            return new Vector3f(0,0,0);
        }
    }

}

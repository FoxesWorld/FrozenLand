package org.foxesworld.newgame.engine.player.input;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.foxesworld.newgame.engine.player.camera.ShakeCam;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class UserInputHandler extends AbstractControl implements ActionListener, AnalogListener {

    private final Vector3f walkDirection = new Vector3f();
    private final float walkSpeed = 4.0f;
    private final float runSpeed = 8.0f;
    private final float rotationSpeed = 0.5f;
    private final InputManager inputManager;
    private final Camera cam;
    private final Node rootNode;
    private BetterCharacterControl characterControl;
    private float walkAudioCooldown = 0.0f;
    private final float walkAudioCooldownDuration = 0.5f;
    private AudioNode walkAudio;

    private Runnable attackCallback;
    private boolean isWalking;
    private boolean ready = false;
    private float isInAir = 0;
    private boolean left, right, up, down, run;
    private AssetManager assetManager;
    //private Sound SOUND;
    static HashMap<String, List<Object>> userInputConfig;

    public UserInputHandler(InputManager inputManager, AssetManager assetManager, Camera cam, Node rootNode, Runnable attackCallback, HashMap<String, List<Object>> userInputConfig) {
        this.inputManager = inputManager;
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.attackCallback = attackCallback;
        this.cam = cam;
        this.userInputConfig = userInputConfig;
        //this.SOUND = NewGame.getSOUND();
    }

    private void initialize() {
        if (ready) return;
        ready = true;

        characterControl = spatial.getControl(BetterCharacterControl.class);
        if (characterControl == null) {
            System.err.println(getClass() + " can be attached only to a spatial that has a BetterCharacterControl");
            return;
        }

        inputManager.setCursorVisible(false);
        keyBindingInit(UserInputHelper.getInputMaps(userInputConfig));

        // Создайте AudioNode и добавьте его в rootNode только один раз
        //walkAudio = SOUND.getSoundNode("player/walkAudio", false, 5.0f, 10.0f, 1.0f);
        //walkAudio.setLocalTranslation(spatial.getLocalTranslation());
        //rootNode.attachChild(walkAudio);
    }

    protected void keyBindingInit(Stack<String> inputMaps) {
        inputMaps.forEach(inputMap -> userInputConfig.get(inputMap).forEach(inputLine -> {
            InputType inputType = InputType.valueOf(inputMap.toUpperCase());
            int inputKey;
            String inputName;
            boolean negative;

            switch (inputType) {
                case KEYBOARD:
                    inputKey = (Integer) ((HashMap<?, ?>) inputLine).get("inputKey");
                    inputName = (String) ((HashMap<?, ?>) inputLine).get("inputName");
                    inputManager.addMapping(inputName, new KeyTrigger(inputKey));
                    inputManager.addListener(this, inputName);
                    break;

                case MOUSEAXIS:
                    inputKey = (Integer) ((HashMap<?, ?>) inputLine).get("inputKey");
                    inputName = (String) ((HashMap<?, ?>) inputLine).get("inputName");
                    negative = (Boolean) ((HashMap<?, ?>) inputLine).get("negative");
                    inputManager.addMapping(inputName, new MouseAxisTrigger(inputKey, negative));
                    inputManager.addListener(this, inputName);
                    break;

                case MOUSEBUTTONS:
                    inputKey = (Integer) ((HashMap<?, ?>) inputLine).get("inputKey");
                    inputName = (String) ((HashMap<?, ?>) inputLine).get("inputName");
                    inputManager.addMapping(inputName, new MouseButtonTrigger(inputKey));
                    inputManager.addListener(this, inputName);
                    break;
            }
        }));
    }
    final float angles[] = {0, 0, 0};
    final Quaternion tmpRot = new Quaternion();

    @Override
    public void onAnalog(String name, float value, float tpf) {
        value = value * rotationSpeed;
        switch (name) {

            case "Rotate_Left": {
                angles[1] += value;
                break;
            }

            case "Rotate_Right": {
                angles[1] -= value;
                break;
            }

            case "Rotate_Up": {
                angles[0] -= value;
                break;
            }

            case "Rotate_Down": {
                angles[0] += value;
                break;
            }

        }
        if (angles[0] > 1.1) angles[0] = 1.1f;
        if (angles[0] < -0.85) angles[0] = -0.85f;

        Vector3f v = characterControl.getViewDirection();
        v.set(Vector3f.UNIT_Z);
        tmpRot.fromAngles(angles);
        tmpRot.multLocal(v);
        characterControl.setViewDirection(v);
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case "Left": {
                left = isPressed;
                break;
            }
            case "Right": {
                right = isPressed;
                break;
            }
            case "Up": {
                up = isPressed;
                break;
            }
            case "Down": {
                down = isPressed;
                break;
            }

            case "spawnGhoul": {
                //app.getEnemies(5).add(new Ghoul(app));
                break;
            }

            case "Attack": {
                if (isPressed) {
                    attackCallback.run();
                }
                break;

            }
            case "Jump": {
                if (isPressed) {
                    characterControl.jump();
                    characterControl.setPhysicsDamping(0);
                }
                break;
            }
            case "Run": {
                run = isPressed;
                break;
            }
        }
    }

    private final Quaternion tmpQtr = new Quaternion();
    private final Vector3f tmpV3 = new Vector3f();

    @Override
    protected void controlUpdate(float tpf) {

        initialize();
        // Using a float here helps us achieve smoother transitions on rough terrains.
        if (!characterControl.isOnGround()) {
            isInAir += tpf;
            // При взлете или падении отключим звук ходьбы
            if (walkAudio != null && walkAudio.getStatus().equals(AudioSource.Status.Playing)) {
                walkAudio.stop();
            }
        } else {
            isInAir = 0;
            //playerMovement();

            // Уменьшаем время cooldown
            if (walkAudioCooldown > 0) {
                walkAudioCooldown -= tpf;
            }

            float speed = run ? runSpeed : walkSpeed;
            walkDirection.set(0, 0, 0);
            if (left) walkDirection.addLocal(1, 0, 0);
            if (right) walkDirection.addLocal(-1, 0, 0);
            if (up) walkDirection.addLocal(0, 0, 1);
            if (down) walkDirection.addLocal(0, 0, -1);

            tmpV3.set(characterControl.getViewDirection());
            tmpV3.y = 0; // Remove y component
            tmpV3.normalizeLocal();

            tmpQtr.loadIdentity();
            tmpQtr.lookAt(tmpV3, Vector3f.UNIT_Y);
            tmpQtr.multLocal(walkDirection);
            walkDirection.multLocal(speed);

            characterControl.setWalkDirection(walkDirection);
            characterControl.setPhysicsDamping(0.9f);
        }
    }

    private void playerMovement() {
        if (walkDirection.lengthSquared() > 0) {
            isWalking = true;
            if (!walkAudio.getStatus().equals(AudioSource.Status.Playing) && walkAudioCooldown <= 0) {
                // Воспроизведем звук ходьбы, если еще не воспроизводится и прошло время cooldown
                walkAudio.play();
                walkAudioCooldown = walkAudioCooldownDuration;
            }
        } else {
            isWalking = false;
            if (walkAudio.getStatus().equals(AudioSource.Status.Playing)) {
                // Остановим звук ходьбы, если игрок перестал двигаться
                walkAudio.stop();
                walkAudioCooldown = 0;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

}
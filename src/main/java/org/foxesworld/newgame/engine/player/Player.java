package org.foxesworld.newgame.engine.player;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.foxesworld.newgame.engine.Kernel;
import org.foxesworld.newgame.engine.player.camera.CameraFollowSpatial;
import org.foxesworld.newgame.engine.player.camera.ShakeCam;
import org.foxesworld.newgame.engine.player.input.FPSViewControl;
import org.foxesworld.newgame.engine.player.input.UserInputHandler;
import org.foxesworld.newgame.engine.providers.sound.SoundManager;

import java.util.Map;

public class Player extends Node implements PlayerInterface {

    private BetterCharacterControl characterControl;
    private  UserInputHandler userInputHandler;
    private Vector3f jumpForce = new Vector3f(0, 300, 0);
    private AssetManager assetManager;
    private  AppStateManager stateManager;
    private Spatial actorLoad;
    private SoundManager soundManager;
    private  NiftyJmeDisplay niftyDisplay;
    private InputManager inputManager;
    private Node rootNode;
    private PhysicsSpace pspace;
    private Map CFG;

    public Player(Kernel kernel){
        this.stateManager = kernel.getStateManager();
        this.soundManager = kernel.getSoundManager();
        this.niftyDisplay = kernel.getNiftyDisplay();
        this.assetManager = kernel.getAssetManager();
        this.rootNode = kernel.getRootNode();
        this.pspace = kernel.getBulletAppState().getPhysicsSpace();
        this.inputManager = kernel.getInputManager();
        this.CFG = kernel.getCONFIG();

        actorLoad = assetManager.loadModel("Models/char.glb");
        actorLoad.setLocalScale(1f);
        this.attachChild(actorLoad);
        this.setCullHint(CullHint.Never);
    }

    public void addPlayer(Camera cam, Vector3f spawnPoint){
        Player fpsPlayer = (Player) this.clone();
        Camera fpsCam = cam.clone();
        this.loadFPSLogicWorld(cam, fpsCam, fpsPlayer, spawnPoint);
        fpsPlayer.loadFPSLogicFPSView(cam, fpsCam, this);
        pspace.addAll(this);
        rootNode.attachChild(this);
    }
    public void loadFPSLogicWorld(Camera cam, Camera fpsCam, Spatial playerModel, Vector3f spawnPoint){
        BoundingBox jesseBbox=(BoundingBox)getWorldBound();
        characterControl = new BetterCharacterControl(jesseBbox.getXExtent(), jesseBbox.getYExtent(), 50f);
        characterControl.setJumpForce(jumpForce);
        addControl(characterControl);

        // Spawn position
        characterControl.warp(spawnPoint);

        ShakeCam camShake = new ShakeCam(cam);
        stateManager.attach(camShake);

        // Load character logic
        addControl(userInputHandler = new UserInputHandler(this, cam, ()->
        playerModel.getControl(ActionsControl.class).shot(assetManager,cam.getLocation().add(cam.getDirection().mult(1)),cam.getDirection(),this.rootNode, this.pspace)));
        addControl(new CameraFollowSpatial(getUserInputHandler(), cam, camShake));
        addControl(new ActionsControl(assetManager,soundManager));
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
    }

    public void loadFPSLogicFPSView(Camera cam, Camera fpsCam, Spatial playerSpatial){
        addControl(new AbstractControl(){
            @Override
            protected void controlUpdate(float tpf) {
                setLocalTransform(playerSpatial.getWorldTransform());
                fpsCam.setLocation(cam.getLocation());
                fpsCam.lookAtDirection(cam.getDirection(),cam.getUp());
            }
            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) { }
        });
        addControl(new FPSViewControl(FPSViewControl.Mode.FPS_SCENE));
        addControl(new ActionsControl(assetManager,soundManager,playerSpatial.getControl(BetterCharacterControl.class)));
    }

    public Vector3f getPlayerPosition(){
        return userInputHandler.getPlayerPosition();
    }

    public BetterCharacterControl getCharacterControl() {
        return characterControl;
    }

    public UserInputHandler getUserInputHandler() {
        return userInputHandler;
    }

    public Vector3f getJumpForce() {
        return jumpForce;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AppStateManager getStateManager() {
        return stateManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public NiftyJmeDisplay getNiftyDisplay() {
        return niftyDisplay;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public PhysicsSpace getPspace() {
        return pspace;
    }

    public Map getCFG() {
        return CFG;
    }
}
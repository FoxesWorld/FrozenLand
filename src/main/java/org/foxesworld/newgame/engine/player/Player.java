package org.foxesworld.newgame.engine.player;

import codex.j3map.J3map;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.player.camera.CameraFollowSpatial;
import org.foxesworld.newgame.engine.player.camera.ShakeCam;
import org.foxesworld.newgame.engine.player.input.FPSViewControl;
import org.foxesworld.newgame.engine.player.input.UserInputHandler;
import org.foxesworld.newgame.engine.providers.sound.SoundProvider;

import java.util.Map;

public class Player extends Node implements PlayerInterface {

    private BetterCharacterControl characterControl;
    private  Camera fpsCam;
    private  UserInputHandler userInputHandler;
    private Vector3f jumpForce;
    private AssetManager assetManager;
    private  AppStateManager stateManager;
    private Spatial actorLoad;
    private SoundProvider soundProvider;
    private InputManager inputManager;
    private Node rootNode;
    private  Node guiNode;
    private PhysicsSpace pspace;
    private Map CFG;
    private J3map playerSpecs;

    public Player(KernelInterface kernel){
        this.stateManager = kernel.appStateManager();
        this.soundProvider = kernel.getSoundManager();
        this.assetManager = kernel.getAssetManager();
        this.rootNode = kernel.getRootNode();
        this.guiNode = kernel.getGuiNode();
        this.pspace = kernel.getBulletAppState().getPhysicsSpace();
        this.inputManager = kernel.getInputManager();
        this.CFG = kernel.getCONFIG();

        playerSpecs = (J3map)assetManager.loadAsset("properties/player.j3map");
        jumpForce = new Vector3f(0, playerSpecs.getFloat("jumpForce"), 0);
        actorLoad = assetManager.loadModel(playerSpecs.getString("model"));
        actorLoad.setLocalScale(playerSpecs.getFloat("scale"));
        this.attachChild(actorLoad);
        this.setCullHint(CullHint.valueOf(playerSpecs.getString("cullHint")));
        this.setShadowMode(RenderQueue.ShadowMode.valueOf(playerSpecs.getString("shadowMode")));
    }

    public void addPlayer(Camera cam, Vector3f spawnPoint){
        Player fpsPlayer = (Player) this.clone();
        fpsCam = cam.clone();
        this.loadFPSLogicWorld(cam, fpsPlayer, spawnPoint);
        fpsPlayer.loadFPSLogicFPSView(cam, fpsCam, this);
        pspace.addAll(this);
        rootNode.attachChild(this);
    }
    public void loadFPSLogicWorld(Camera cam, Spatial playerModel, Vector3f spawnPoint){
        BoundingBox jesseBbox=(BoundingBox)getWorldBound();
        characterControl = new BetterCharacterControl(jesseBbox.getXExtent(), jesseBbox.getYExtent(), playerSpecs.getFloat("mass"));
        characterControl.setJumpForce(jumpForce);
        addControl(characterControl);

        // Spawn position
        characterControl.warp(spawnPoint);

        ShakeCam camShake = new ShakeCam(cam);
        stateManager.attach(camShake);

        // Load playerSpecs logic
        addControl(userInputHandler = new UserInputHandler(this, ()->
        playerModel.getControl(ActionsControl.class).shot(assetManager,cam.getLocation().add(cam.getDirection().mult(1)),cam.getDirection(),this.rootNode, this.pspace)));
        addControl(new CameraFollowSpatial(getUserInputHandler(), cam, camShake));
        addControl(new ActionsControl(assetManager, soundProvider));
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
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
        addControl(new ActionsControl(this));
    }
    @Override
    public Vector3f getPlayerPosition(){
        return userInputHandler.getPlayerPosition();
    }
    @Override
    public Camera getFpsCam(){ return this.fpsCam;}
    @Override
    public BetterCharacterControl getCharacterControl() {
        return characterControl;
    }
    @Override
    public UserInputHandler getUserInputHandler() {
        return userInputHandler;
    }
    @Override
    public Vector3f getJumpForce() {
        return jumpForce;
    }
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }
    @Override
    public AppStateManager getStateManager() {return stateManager;}
    @Override
    public SoundProvider getSoundManager() {
        return soundProvider;
    }
    @Override
    public InputManager getInputManager() {
        return inputManager;
    }
    @Override
    public Node getRootNode() {
        return rootNode;
    }
    @Override
    public Node getGuiNode() {
        return guiNode;
    }
    @Override
    public Map getCFG() {
        return CFG;
    }
}
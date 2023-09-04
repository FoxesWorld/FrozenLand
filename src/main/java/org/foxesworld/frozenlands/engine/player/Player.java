package org.foxesworld.frozenlands.engine.player;

import codex.j3map.J3map;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.foxesworld.frozenlands.engine.KernelInterface;
import org.foxesworld.frozenlands.engine.player.camera.CameraFollowSpatial;
import org.foxesworld.frozenlands.engine.player.input.FPSViewControl;
import org.foxesworld.frozenlands.engine.player.input.UserInputHandler;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public class Player extends Node implements PlayerInterface {

    private  KernelInterface kernelInterface;
    private PlayerOptions playerOptions;
    private  PlayerModel playerModel;
    private  UserInputHandler userInputHandler;
    private SoundProvider soundProvider;
    private PhysicsSpace pspace;

    public Player(KernelInterface kernel) {
        this.kernelInterface = kernel;
        this.soundProvider = kernel.getSoundManager();
        this.pspace = kernel.getBulletAppState().getPhysicsSpace();

        playerOptions = new PlayerOptions((J3map) kernel.getAssetManager().loadAsset("properties/player.j3map"));
        playerModel = new PlayerModel(kernel.getAssetManager(), playerOptions);
        playerModel.setCullHint(playerOptions.getCullHint());
        playerModel.setShadowMode(playerOptions.getShadowMode());
        this.attachChild(playerModel);
    }

    private  void onSpawn(PlayerInterface player) {
       player.getSoundManager().getRandomAudioNode(player.getPlayerSounds().get("spawn")).play();
    }

    public void addPlayer(Camera cam, Vector3f spawnPoint){
        Player fpsPlayer = (Player) this.clone();
        playerOptions.setFpsCam(cam.clone());
        this.loadFPSLogicWorld(cam, fpsPlayer, spawnPoint);
        fpsPlayer.loadFPSLogicFPSView(cam, playerOptions.getFpsCam(), this.playerModel.getPlayerSpatial());
        pspace.addAll(this);
        kernelInterface.getRootNode().attachChild(this);
    }
    public void loadFPSLogicWorld(Camera cam, Spatial playerModel, Vector3f spawnPoint){
        BoundingBox jesseBbox=(BoundingBox)getWorldBound();
        playerOptions.setCharacterControl(new BetterCharacterControl(jesseBbox.getXExtent(), jesseBbox.getYExtent(), playerOptions.getMass()));
        playerOptions.getCharacterControl().setJumpForce(playerOptions.getJumpForce());
        addControl(playerOptions.getCharacterControl());

        // Spawn position
        playerOptions.getCharacterControl().warp(spawnPoint);

        userInputHandler = new UserInputHandler(this, ()-> playerModel.getControl(ActionsControl.class).shot(kernelInterface.getAssetManager(),cam.getLocation().add(cam.getDirection().mult(1)),cam.getDirection(),kernelInterface.getRootNode(), this.pspace));
        userInputHandler.setPlayerHealth(playerOptions.getInitialHealth());
        
        // Load playerOptions logic
        addControl(userInputHandler);
        addControl(new CameraFollowSpatial(getUserInputHandler(), cam));
        addControl(new ActionsControl(kernelInterface.getAssetManager(), soundProvider));
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
        this.onSpawn(this);
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
    public Logger getLogger(){ return kernelInterface.getLogger();}
    @Override
    public Vector3f getPlayerPosition(){
        return userInputHandler.getPlayerPosition();
    }
    @Override
    public UserInputHandler getUserInputHandler() {
        return userInputHandler;
    }
    @Override
    public AssetManager getAssetManager() {
        return kernelInterface.getAssetManager();
    }
    @Override
    public AppStateManager getStateManager() {return kernelInterface.appStateManager();}
    @Override
    public SoundProvider getSoundManager() {
        return soundProvider;
    }
    @Override
    public Map<String, List<AudioNode>> getPlayerSounds() {
        return userInputHandler.getPlayerSounds();
    }
    @Override
    public InputManager getInputManager() {
        return kernelInterface.getInputManager();
    }
    @Override
    public Node getRootNode() {
        return kernelInterface.getRootNode();
    }
    @Override
    public Node getGuiNode() {
        return kernelInterface.getGuiNode();
    }
    @Override
    public Map getConfig() {
        return kernelInterface.getConfig();
    }
    @Override
    public PlayerOptions getPlayerOptions() {
        return playerOptions;
    }
    @Override
    public PlayerModel getPlayerModel() {
        return playerModel;
    }
}
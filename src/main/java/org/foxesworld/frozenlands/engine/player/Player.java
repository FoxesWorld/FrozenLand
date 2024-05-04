package org.foxesworld.frozenlands.engine.player;

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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.foxesworld.frozenlands.engine.Kernel;
import org.foxesworld.frozenlands.engine.player.camera.CameraFollowSpatial;
import org.foxesworld.frozenlands.engine.player.input.FPSViewControl;
import org.foxesworld.frozenlands.engine.player.input.UserInputHandler;

import java.util.Map;

public class Player extends Node implements PlayerInterface {

    private Kernel kernel;
    private PlayerOptions playerOptions;
    private PlayerSoundProvider playerSoundProvider;
    private PlayerModel playerModel;
    private UserInputHandler userInputHandler;
    private PhysicsSpace pspace;

    public Player(Kernel kernel) {
        this.kernel = kernel;
        this.pspace = kernel.getBulletAppState().getPhysicsSpace();

        playerOptions = new PlayerOptions((J3map) kernel.getAssetManager().loadAsset("properties/player.j3map"));
        playerSoundProvider = new PlayerSoundProvider(kernel);
        playerModel = new PlayerModel(kernel.getAssetManager(), playerOptions);
        playerModel.setCullHint(playerOptions.getCullHint());
        playerModel.setShadowMode(playerOptions.getShadowMode());
        this.attachChild(playerModel);
    }

    private void onSpawn() {
        this.playerSoundProvider.playSound("spawn");
    }

    public void addPlayer(Camera cam, Vector3f spawnPoint) {
        Player fpsPlayer = (Player) this.clone();
        playerOptions.setFpsCam(cam.clone());
        this.loadFPSLogicWorld(cam, fpsPlayer, spawnPoint);
        fpsPlayer.loadFPSLogicFPSView(cam, this.playerOptions.getFpsCam(), this.playerModel.getPlayerSpatial());
        pspace.addAll(this);
        kernel.getRootNode().attachChild(this);
    }

    public void loadFPSLogicWorld(Camera cam, Spatial playerModel, Vector3f spawnPoint){
        BoundingBox jesseBbox=(BoundingBox)getWorldBound();
        System.out.println("Radius " + jesseBbox.getXExtent() + " Height " + jesseBbox.getYExtent());
        playerOptions.setCharacterControl(new BetterCharacterControl(jesseBbox.getXExtent(), jesseBbox.getYExtent() * 4, playerOptions.getMass()));
        playerOptions.getCharacterControl().setJumpForce(playerOptions.getJumpForce());
        addControl(playerOptions.getCharacterControl());

        // Spawn position
        playerOptions.getCharacterControl().warp(spawnPoint);

        userInputHandler = new UserInputHandler(this, ()-> playerModel.getControl(ActionsControl.class).shot(kernel.getAssetManager(),cam.getLocation().add(cam.getDirection().mult(1)),cam.getDirection(),kernel.getRootNode(), this.pspace));
        this.setPlayerHealth(playerOptions.getInitialHealth());

        // Load playerOptions logic
        addControl(userInputHandler);
        addControl(new CameraFollowSpatial(getUserInputHandler(), cam));
        addControl(new ActionsControl(this));
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
        this.onSpawn();
    }

    public void loadFPSLogicFPSView(Camera cam, Camera fpsCam, Spatial playerSpatial) {
        addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                setLocalTransform(playerSpatial.getWorldTransform());
                fpsCam.setLocation(cam.getLocation());
                fpsCam.lookAtDirection(cam.getDirection(), cam.getUp());
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
        addControl(new ActionsControl(this));
    }

    @Override
    public Vector3f getPlayerPosition() {
        return userInputHandler.getPlayerPosition();
    }

    @Override
    public UserInputHandler getUserInputHandler() {
        return userInputHandler;
    }

    @Override
    public AssetManager getAssetManager() {
        return kernel.getAssetManager();
    }

    @Override
    public AppStateManager getStateManager() {
        return kernel.appStateManager();
    }

    @Override
    public InputManager getInputManager() {
        return kernel.getInputManager();
    }

    @Override
    public Node getRootNode() {
        return kernel.getRootNode();
    }

    @Override
    public Node getGuiNode() {
        return kernel.getGuiNode();
    }

    @Override
    public Map getConfig() {
        return kernel.getConfig();
    }

    @Override
    public PlayerOptions getPlayerOptions() {
        return playerOptions;
    }

    @Override
    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    @Override
    public PlayerSoundProvider getPlayerSoundProvider() {
        return playerSoundProvider;
    }

    public void setPlayerHealth(int health){
        this.playerOptions.setInitialHealth(health);
    }
}
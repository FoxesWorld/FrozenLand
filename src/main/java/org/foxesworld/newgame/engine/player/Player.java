package org.foxesworld.newgame.engine.player;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
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
import org.foxesworld.newgame.engine.player.camera.CameraFollowSpatial;
import org.foxesworld.newgame.engine.player.camera.ShakeCam;
import org.foxesworld.newgame.engine.player.input.FPSViewControl;
import org.foxesworld.newgame.engine.player.input.UserInputHandler;
import org.foxesworld.newgame.engine.sound.SoundManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Node {

    private Vector3f jumpForce = new Vector3f(0, 300, 0);
    private AssetManager assetManager;
    private  AppStateManager stateManager;
    private SoundManager soundManager;
    private  NiftyJmeDisplay niftyDisplay;
    private InputManager inputManager;
    private Node rootNode;
    private PhysicsSpace pspace;
    private Map CFG;

    public Player(AppStateManager stateManager, NiftyJmeDisplay niftyDisplay, SoundManager soundManager, AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, InputManager inputManager, Map config){
        this.stateManager = stateManager;
        this.soundManager = soundManager;
        this.niftyDisplay = niftyDisplay;
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.pspace = bulletAppState.getPhysicsSpace();
        this.inputManager = inputManager;
        this.CFG = config;

        Spatial actorLoad = assetManager.loadModel("models/char.glb");
        actorLoad.setLocalScale(1f);
        attachChild(actorLoad);
        setCullHint(CullHint.Never);
    }

    public void addPlayer(Camera cam, Vector3f spawnPoint){
        Player fpsPlayer = (Player) this.clone();
        Camera fpsCam = cam.clone();
        this.loadFPSLogicWorld(cam, fpsCam, fpsPlayer, spawnPoint);
        fpsPlayer.loadFPSLogicFPSView(cam, fpsCam, this);
    }
    public void loadFPSLogicWorld(Camera cam, Camera fpsCam, Spatial playerModel, Vector3f spawnPoint){
        BoundingBox jesseBbox=(BoundingBox)getWorldBound();
        BetterCharacterControl characterControl = new BetterCharacterControl(jesseBbox.getXExtent(), jesseBbox.getYExtent(), 50f);
        characterControl.setJumpForce(jumpForce);
        addControl(characterControl);

        // Установка позиции спавна игрока
        characterControl.warp(spawnPoint);

        ShakeCam camShake = new ShakeCam(cam);
        stateManager.attach(camShake);
        camShake.shakeHitHard();

        // Load character logic
        addControl(new UserInputHandler(niftyDisplay, soundManager, inputManager, assetManager, cam, rootNode,()->
        playerModel.getControl(ActionsControl.class).shot(assetManager,cam.getLocation().add(cam.getDirection().mult(1)),cam.getDirection(),this.rootNode, this.pspace), (HashMap<String, List<Object>>) CFG.get("userInput")));
        addControl(new CameraFollowSpatial(cam));
        addControl(new ActionsControl(assetManager,true));
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
    }

    public void loadFPSLogicFPSView(Camera cam, Camera fpsCam, Spatial jesse){
        addControl(new AbstractControl(){
            protected void controlUpdate(float tpf) {
                setLocalTransform(jesse.getWorldTransform());
                fpsCam.setLocation(cam.getLocation());
                fpsCam.lookAtDirection(cam.getDirection(),cam.getUp());
            }
            protected void controlRender(RenderManager rm, ViewPort vp) { }
        });
        addControl(new FPSViewControl(FPSViewControl.Mode.FPS_SCENE));
        addControl(new ActionsControl(assetManager,true,jesse.getControl(BetterCharacterControl.class)));
    }

    public  Vector3f getPosition(){
        return this.getPosition();
    }
}
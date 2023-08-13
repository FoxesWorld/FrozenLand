package org.foxesworld.newgame.engine.player;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
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
import org.foxesworld.newgame.engine.player.camera.CameraFollowSpatial;
import org.foxesworld.newgame.engine.player.camera.CameraSwingControl;
import org.foxesworld.newgame.engine.player.input.FPSViewControl;
import org.foxesworld.newgame.engine.player.input.UserInputHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Node {

    private Vector3f jumpForce = new Vector3f(0, 300, 0);
    private AssetManager assetManager;
    private InputManager inputManager;
    private Node rootNode;
    private PhysicsSpace pspace;
    private Map CFG;

    public Player(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, InputManager inputManager,  Map config){
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.pspace = bulletAppState.getPhysicsSpace();
        this.inputManager = inputManager;
        this.CFG = config;

        Spatial actorLoad = assetManager.loadModel("models/Jesse.glb");
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
    public void loadFPSLogicWorld(Camera cam, Camera fpsCam, Spatial fpsJesse, Vector3f spawnPoint){
        BoundingBox jesseBbox=(BoundingBox)getWorldBound();
        BetterCharacterControl characterControl = new BetterCharacterControl(jesseBbox.getXExtent(), jesseBbox.getYExtent(), 50f);
        characterControl.setJumpForce(jumpForce);
        addControl(characterControl);

        // Установка позиции спавна игрока
        characterControl.warp(spawnPoint);

        // Load character logic
        addControl(new UserInputHandler(inputManager, assetManager, cam, rootNode,()->{
            fpsJesse.getControl(ActionsControl.class).shot( assetManager,cam.getLocation().add(cam.getDirection().mult(1)),cam.getDirection(),this.rootNode, this.pspace);
        }, (HashMap<String, List<Object>>) CFG.get("userInput")));
        addControl(new CameraFollowSpatial(cam));
        addControl(new ActionsControl(assetManager,true));
        addControl(new FPSViewControl(FPSViewControl.Mode.WORLD_SCENE));
        addControl(new CameraSwingControl(cam));
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
}
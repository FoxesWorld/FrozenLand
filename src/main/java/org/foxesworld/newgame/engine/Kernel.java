package org.foxesworld.newgame.engine;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.foxesworld.newgame.engine.material.MaterialManager;
import org.foxesworld.newgame.engine.player.Player;
import org.foxesworld.newgame.engine.shaders.Bloom;
import org.foxesworld.newgame.engine.shaders.DOF;
import org.foxesworld.newgame.engine.shaders.LSF;
import org.foxesworld.newgame.engine.sound.SoundManager;
import org.foxesworld.newgame.engine.world.skybox.SkyboxGenerator;
import org.foxesworld.newgame.engine.world.sun.LightingType;
import org.foxesworld.newgame.engine.world.sun.Sun;
import org.foxesworld.newgame.engine.world.terrain.TerrainGenerator;

import java.util.Map;

public class Kernel {

    private  Map CONFIG;
    protected  AssetManager assetManager;
    protected ViewPort viewPort;
    protected  NiftyJmeDisplay niftyDisplay;
    protected SoundManager soundManager;
    protected  MaterialManager materialManager;
    protected Camera camera;
    protected BulletAppState bulletAppState;
    protected Node rootNode;
    protected FilterPostProcessor fpp;
    protected InputManager inputManager;

    public Kernel(NiftyJmeDisplay niftyDisplay, ViewPort viewPort, AssetManager assetManager, Camera camera, Node rootNode, FilterPostProcessor fpp, InputManager inputManager, BulletAppState bulletAppState, Map CONFIG) {
        this.assetManager = assetManager;
        this.viewPort = viewPort;
        this.niftyDisplay = niftyDisplay;
        this.soundManager = new SoundManager(assetManager);
        this.materialManager = new MaterialManager(assetManager);
        this.camera = camera;
        this.rootNode = rootNode;
        this.fpp = fpp;
        this.inputManager = inputManager;
        this.bulletAppState = bulletAppState;
        this.CONFIG = CONFIG;
        this.genSkyBox("textures/BrightSky.dds");
        TerrainGenerator terrain = new TerrainGenerator(rootNode, bulletAppState);
        terrain.generateHillyTerrain(new Vector3f(0,0,0), materialManager.createMat("textures/soil"));
        Player player = new Player(niftyDisplay, soundManager, assetManager, rootNode, bulletAppState, inputManager, CONFIG);
        player.addPlayer(camera, new Vector3f(0,5,0));
        bulletAppState.getPhysicsSpace().addAll(player);
        rootNode.attachChild(player);
        this.addSun();
        addShaders(fpp);
    }

    private void genSkyBox(String texture){
        SkyboxGenerator sky = new SkyboxGenerator(assetManager);
        Spatial skybox = sky.generateSkybox(texture);
        rootNode.attachChild(skybox);
    }

    private void addSun(){
        Sun sun = new Sun(assetManager, rootNode, "sun", LightingType.AMBIENT, ColorRGBA.White, 1f);
        sun.setSunOptions(new Vector3f(5,5,5),3f);
        sun.setPosition(new Vector3f(0f, 50f, 0f));
        sun.addSun(new MaterialManager(assetManager).createMat("textures/sun"));
    }

    private void  addShaders(FilterPostProcessor fpp){
        Bloom bloom = new Bloom(fpp);
        bloom.setBloom(1.0f);
        bloom.setExposurePover(60);
        bloom.compile();

        //Light Scattering Filter
        LSF lsf = new LSF(fpp,new Vector3f(-4.9236743f, -1.27054665f, 5.896916f).mult(-300));
        lsf.setLightDensity(0.5f);
        lsf.compile();

        //Depth of field Filter
        DOF dof = new DOF(fpp);
        dof.setFocusDistance(0);
        dof.setFocusRange(100);
        dof.compile();

        fpp.addFilter(new FXAAFilter());

        viewPort.addProcessor(fpp);
    }

}

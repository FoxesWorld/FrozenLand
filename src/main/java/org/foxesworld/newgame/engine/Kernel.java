package org.foxesworld.newgame.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.foxesworld.newgame.NewGame;
import org.foxesworld.newgame.engine.ai.NPC;
import org.foxesworld.newgame.engine.ai.NPCAI;
import org.foxesworld.newgame.engine.discord.Discord;
import org.foxesworld.newgame.engine.player.Player;
import org.foxesworld.newgame.engine.providers.material.MaterialManager;
import org.foxesworld.newgame.engine.providers.model.ModelManager;
import org.foxesworld.newgame.engine.providers.sound.SoundManager;
import org.foxesworld.newgame.engine.shaders.Bloom;
import org.foxesworld.newgame.engine.shaders.DOF;
import org.foxesworld.newgame.engine.shaders.LSF;
import org.foxesworld.newgame.engine.world.sky.DynamicSky;
import org.foxesworld.newgame.engine.world.sun.LightingType;
import org.foxesworld.newgame.engine.world.sun.Sun;
import org.foxesworld.newgame.engine.world.terrain.TerrainManager;
import org.foxesworld.newgame.engine.world.terrain.TerrainManagerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Kernel extends NewGame implements KernelInterface {

    private final Logger logger = LoggerFactory.getLogger(Kernel.class);
    private final Map CONFIG;
    protected final AssetManager assetManager;
    protected final Discord discord;
    protected final ViewPort viewPort;
    protected NiftyJmeDisplay niftyDisplay;
    protected AppStateManager stateManager;
    protected SoundManager soundManager;
    protected MaterialManager materialManager;
    protected ModelManager modelManager;

    protected Camera camera;
    protected BulletAppState bulletAppState;

    protected Node rootNode;
    protected FilterPostProcessor fpp;
    protected InputManager inputManager;
    protected DynamicSky sky;
    private TerrainManagerInterface terrainManager;

    protected Player player;
    NPCAI npcAI;

    public Kernel(NewGame newGame, NiftyJmeDisplay niftyDisplay, FilterPostProcessor fpp, BulletAppState bulletAppState, Map CONFIG) {
        this.stateManager = newGame.getStateManager();
        this.assetManager = newGame.getAssetManager();
        this.inputManager = newGame.getInputManager();
        this.camera = newGame.getCamera();
        this.rootNode = newGame.getRootNode();
        this.viewPort = newGame.getViewPort();
        this.niftyDisplay = niftyDisplay;
        this.soundManager = new SoundManager(this);
        this.materialManager = new MaterialManager(this);
        this.modelManager = new ModelManager(assetManager, bulletAppState, rootNode);

        this.fpp = fpp;
        this.bulletAppState = bulletAppState;
        this.CONFIG = CONFIG;
        this.discord = new Discord("Infinite world with border", this.getClass().getTypeName());
        this.discord.discordRpcStart("default");
        this.genSkyBox();

        terrainManager = new TerrainManager(this);
        rootNode.attachChild(terrainManager.getTerrain());
        rootNode.attachChild(terrainManager.getMountains());
        //this.addSun();
        addShaders(fpp);

        player = new Player(this);
        player.addPlayer(camera, new Vector3f(0,300,0));

    }

    private void genSkyBox() {
        sky = new DynamicSky(assetManager, viewPort, rootNode);
    }

    private void addSun() {
        Sun sun = new Sun(assetManager, rootNode, "sun", LightingType.DIRECTIONAL, ColorRGBA.White, 1f);
        sun.setSunOptions(new Vector3f(5, 5, 5), 3f);
        sun.setPosition(new Vector3f(0f, 10f, 0f));
        sun.addSun(materialManager.getMaterial("sun"));
    }

    private void ncpTest() {
        Node npcNode = new Node();
        NPC npc = new NPC(assetManager.loadModel("Models/char.glb"));
        npcNode.attachChild(npc.getModel());
        npcAI = new NPCAI(npc);
        npcAI.setTargetPosition(new Vector3f(50, 0, 0));
        npcNode.addControl(npcAI);
        bulletAppState.getPhysicsSpace().add(npcNode);
        rootNode.attachChild(npcNode);
    }

    private void addShaders(FilterPostProcessor fpp) {
        Bloom bloom = new Bloom(fpp);
        bloom.setBloom(1.0f);
        bloom.setExposurePover(60);
        bloom.compile();

        //Light Scattering Filter
        LSF lsf = new LSF(fpp, new Vector3f(-4.9236743f, -1.27054665f, 5.896916f).mult(-300));
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

    @Override
    public void simpleUpdate(float tpf) {
        sky.updateTime();
    }
    public Map getCONFIG() {
        return CONFIG;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public NiftyJmeDisplay getNiftyDisplay() {
        return niftyDisplay;
    }

    public AppStateManager getStateManager() {
        return stateManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public MaterialManager getMaterialManager() {
        return materialManager;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public Camera getCamera() {
        return camera;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public Node getRootNode() {
        return rootNode;
    }
    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public FilterPostProcessor getFpp() {
        return fpp;
    }
    public InputManager getInputManager() {
        return inputManager;
    }
    public Player getPlayer() {
        return player;
    }
}

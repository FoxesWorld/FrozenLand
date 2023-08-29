package org.foxesworld.newgame.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
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
import org.foxesworld.newgame.engine.world.sky.Sky;
import org.foxesworld.newgame.engine.world.terrain.TerrainManager;
import org.foxesworld.newgame.engine.world.terrain.TerrainManagerInterface;
import org.slf4j.Logger;

import java.util.Map;

public class Kernel extends BaseAppState implements KernelInterface {

    private final NewGame newGame;
    private  final Logger logger;
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
    protected Sky sky;
    private TerrainManagerInterface terrainManager;

    protected Player player;
    @Deprecated
    NPCAI npcAI;

    public Kernel(NewGame newGame) {
        this.newGame = newGame;
        this.stateManager = newGame.getStateManager();
        this.assetManager = newGame.getAssetManager();
        this.inputManager = newGame.getInputManager();
        this.camera = newGame.getCamera();
        this.rootNode = newGame.getRootNode();
        this.viewPort = newGame.getViewPort();
        this.bulletAppState = newGame.getBulletAppState();
        this.fpp = newGame.getFpp();
        this.niftyDisplay = newGame.getNifty();
        this.CONFIG = newGame.getCONFIG();
        this.logger = newGame.getLogger();
        this.soundManager = new SoundManager(this);
        this.materialManager = new MaterialManager(this);
        this.materialManager.addMaterials();
        this.modelManager = new ModelManager(assetManager, bulletAppState, rootNode);

        this.discord = new Discord("Infinite world with border", this.getClass().getTypeName());
        this.discord.discordRpcStart("default");
        this.sky = new Sky(this);
        this.sky.addSky();

        terrainManager = new TerrainManager(this);
        rootNode.attachChild(terrainManager.getTerrain());
        rootNode.attachChild(terrainManager.getMountains());
        addShaders(fpp);

        player = new Player(this);
        player.addPlayer(camera, new Vector3f(0, 300, 0));
    }

    @Override
    protected void initialize(Application application) {}
    @Override
    protected void cleanup(Application application) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
    }

    @Deprecated
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
    @Deprecated
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

        //Shadows EXP
        FilterPostProcessor processor = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter filter = new DirectionalLightShadowFilter(assetManager, 2048, 1);
        filter.setLight(this.sky.getSun());
        processor.addFilter(filter);
        viewPort.addProcessor(processor);
    }
    @Override
    public Map getCONFIG() {
        return CONFIG;
    }
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }
    @Override
    public AppStateManager appStateManager(){ return newGame.getStateManager(); }
    @Override
    public InputManager getInputManager(){ return  newGame.getInputManager();}
    @Deprecated
    public NiftyJmeDisplay getNiftyDisplay() {
        return niftyDisplay;
    }
    @Override
    public SoundManager getSoundManager() {
        return soundManager;
    }
    @Override
    public MaterialManager getMaterialManager() {
        return materialManager;
    }
    @Override
    public Camera getCamera() {
        return camera;
    }
    @Override
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }
    @Override
    public Node getRootNode() {
        return rootNode;
    }
    @Override
    public Logger getLogger() {
        return this.logger;
    }
    @Override
    public Player getPlayer() {
        return player;
    }
}

package org.foxesworld.newgame.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import org.foxesworld.newgame.NewGame;
import org.foxesworld.newgame.engine.discord.Discord;
import org.foxesworld.newgame.engine.player.Player;
import org.foxesworld.newgame.engine.providers.material.MaterialProvider;
import org.foxesworld.newgame.engine.providers.model.ModelpROVIDER;
import org.foxesworld.newgame.engine.providers.sound.SoundProvider;
import org.foxesworld.newgame.engine.shaders.Shaders;
import org.foxesworld.newgame.engine.world.WorldUpdate;
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
    protected AppStateManager stateManager;
    protected SoundProvider soundProvider;
    protected MaterialProvider materialProvider;
    protected ModelpROVIDER modelpROVIDER;

    protected Camera camera;
    protected BulletAppState bulletAppState;
    protected Node rootNode;
    protected Node guiNode;
    protected FilterPostProcessor fpp;
    protected InputManager inputManager;
    protected Sky sky;
    private TerrainManagerInterface terrainManager;

    protected Player player;

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
        this.CONFIG = newGame.getCONFIG();
        this.logger = newGame.getLogger();
        this.guiNode = newGame.getGuiNode();
        this.soundProvider = new SoundProvider(this);
        this.materialProvider = new MaterialProvider(this);
        this.materialProvider.addMaterials();
        this.modelpROVIDER = new ModelpROVIDER(assetManager, bulletAppState, rootNode);

        this.discord = new Discord("In lobby", "Dev Env");
        this.discord.discordRpcStart("frozenLogo");
        this.sky = new Sky(this);
        this.sky.addSky();

        terrainManager = new TerrainManager(this);
        rootNode.attachChild(terrainManager.getTerrain());
        rootNode.attachChild(terrainManager.getMountains());

        player = new Player(this);
        player.addPlayer(camera, new Vector3f(0, 300, 0));
        this.stateManager.attach(new Shaders(this));
        this.stateManager.attach(new WorldUpdate(this));
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
    @Override
    public SoundProvider getSoundManager() {
        return soundProvider;
    }
    @Override
    public MaterialProvider getMaterialManager() {
        return materialProvider;
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
    @Override
    public Sky getSky(){
        return sky;
    }
    @Override
    public ViewPort getViewPort() {
        return this.viewPort;
    }
    @Override
    public Node getGuiNode() {
        return guiNode;
    }
    @Override
    public FilterPostProcessor getFpp() {
        return this.fpp;
    }
}

package org.foxesworld.frozenlands.engine;

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
import org.apache.logging.log4j.Logger;
import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.discord.Discord;
import org.foxesworld.frozenlands.engine.player.Player;
import org.foxesworld.frozenlands.engine.providers.material.MaterialProvider;
import org.foxesworld.frozenlands.engine.providers.model.ModelProvider;
import org.foxesworld.frozenlands.engine.providers.sound.SoundProvider;
import org.foxesworld.frozenlands.engine.shaders.Shaders;
import org.foxesworld.frozenlands.engine.world.WorldUpdate;
import org.foxesworld.frozenlands.engine.world.sky.Sky;
import org.foxesworld.frozenlands.engine.world.terrain.TerrainManager;
import org.foxesworld.frozenlands.engine.world.terrain.TerrainManagerInterface;

import java.util.Map;

public class Kernel extends BaseAppState implements KernelInterface {

    private final FrozenLands frozenLands;
    private final Logger logger;
    private final Map CONFIG;

    protected final AssetManager assetManager;
    protected final Discord discord;
    protected final ViewPort viewPort;
    protected AppStateManager stateManager;
    protected SoundProvider soundProvider;
    protected MaterialProvider materialProvider;
    protected ModelProvider modelProvider;

    protected Camera camera;
    protected BulletAppState bulletAppState;
    protected Node rootNode;
    protected Node guiNode;
    protected FilterPostProcessor fpp;
    protected InputManager inputManager;
    protected Sky sky;
    private TerrainManagerInterface terrainManager;

    protected Player player;

    public Kernel(FrozenLands frozenLands) {
        this.frozenLands = frozenLands;
        this.stateManager = frozenLands.getStateManager();
        this.assetManager = frozenLands.getAssetManager();
        this.inputManager = frozenLands.getInputManager();
        this.camera = frozenLands.getCamera();
        this.rootNode = frozenLands.getRootNode();
        this.viewPort = frozenLands.getViewPort();
        this.bulletAppState = frozenLands.getBulletAppState();
        this.fpp = frozenLands.getFpp();
        this.CONFIG = frozenLands.getCONFIG();
        this.logger = FrozenLands.logger;
        this.guiNode = frozenLands.getGuiNode();

        this.soundProvider = new SoundProvider(this);
        this.soundProvider.loadSounds("data/sounds.json");

        this.materialProvider = new MaterialProvider(this);
        this.materialProvider.loadMaterials("data/materials.json");

        this.modelProvider = new ModelProvider(this);

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
    protected void initialize(Application application) {
    }

    @Override
    protected void cleanup(Application application) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public Map getConfig() {
        return CONFIG;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public AppStateManager appStateManager() {return frozenLands.getStateManager();}

    @Override
    public InputManager getInputManager() {return frozenLands.getInputManager();}

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
    public Player getPlayer() {
        return player;
    }

    @Override
    public Sky getSky() {return sky;}

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

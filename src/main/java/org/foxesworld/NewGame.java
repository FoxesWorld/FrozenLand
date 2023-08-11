package org.foxesworld;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import org.foxesworld.engine.config.ConfigReader;
import org.foxesworld.engine.shaders.Bloom;
import org.foxesworld.engine.shaders.DOF;
import org.foxesworld.engine.shaders.LSF;
import org.foxesworld.engine.sound.Sound;
import org.foxesworld.engine.texture.LoadTexture;
import org.foxesworld.engine.texture.TextureWrap;
import org.foxesworld.engine.world.WorldGen;

import java.util.*;

import static org.foxesworld.engine.solarSystem.sun.LightingType.AMBIENT;
import static org.foxesworld.engine.texture.TextureWrap.REPEAT;

public class NewGame extends SimpleApplication {

    private BulletAppState bulletAppState = new BulletAppState();
    private Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f).mult(-300);
    private FilterPostProcessor fpp;
    private DirectionalLightShadowFilter filter;
    private static Map CONFIG;
    private static Sound SOUND;

    public static void main(String[] args) {
        NewGame app = new NewGame();
        CONFIG = new ConfigReader(new String[]{"userInput", "internal/sounds"}).getCfgMaps();
        AppSettings cfg = new AppSettings(true);
        cfg.setVSync(false);
        cfg.setResolution(1360, 768);
        cfg.setFullscreen(false);
        cfg.setSamples(16);    // anti-aliasing
        cfg.setTitle("SolarSystem"); // branding: window name
        app.setShowSettings(false); // or don't display splashscreen
        app.setDisplayFps(true);
        app.setDisplayStatView(false);
        app.setSettings(cfg);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        SOUND = new Sound(this);
        stateManager.attach(bulletAppState);
        flyCam.setEnabled(false);
        fpp = new FilterPostProcessor(assetManager);
        filter = new DirectionalLightShadowFilter(assetManager, 2048, 1);

        WorldGen world = new WorldGen(this, 256, 2f);

        //TerrainMaterial @Deprecated
                List<Map<TextureWrap, Map<String, String>>> textures = new ArrayList<>();
                textures.add(Collections.singletonMap(REPEAT, Collections.singletonMap("DiffuseMap", "assets/textures/sand/Sand_004_COLOR.png")));
                LoadTexture terrainTexture = new LoadTexture(this, "Common/MatDefs/Terrain/TerrainLighting.j3md");
                System.out.println(terrainTexture.getMaterialDef().getAssetName());
                terrainTexture.addTextures(textures);
                terrainTexture.setMaterialFloat("DiffuseMap_0_scale", 1f);

        world.genTerrain( 1f, 0.01f, 1, 64.0f, terrainTexture.getMaterial());
        world.genWater(lightDir);
        world.addSky("assets/textures/background.dds");
        world.addSun("sun", AMBIENT, ColorRGBA.Gray, new Vector3f(-0.7f, -0.3f, -0.5f).normalizeLocal(), 3f, 2f);
        world.addPlayer(this);

        //Setting shaders
                //Bloom Filter
                Bloom bloom = new Bloom(fpp);
                bloom.setBloom(1.0f);
                bloom.setExposurePover(60);
                bloom.compile();

                //Light Scattering Filter
                LSF lsf = new LSF(fpp,lightDir);
                lsf.setLightDensity(0.5f);
                lsf.compile();

                //Depth of field Filter
                DOF dof = new DOF(fpp);
                dof.setFocusDistance(0);
                dof.setFocusRange(100);
                dof.compile();

                fpp.addFilter(new FXAAFilter());

                int numSamples = getContext().getSettings().getSamples();
                if (numSamples > 0) {
                    fpp.setNumSamples(numSamples);
                }
                viewPort.addProcessor(fpp);
    }

    public BulletAppState getBullet(){
        return bulletAppState;
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Update the player controller each frame
    }
    public static Sound getSOUND() {
        return SOUND;
    }
    public static Map getCONFIG() {
        return CONFIG;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public FilterPostProcessor getFpp(){
        return fpp;
    }

}
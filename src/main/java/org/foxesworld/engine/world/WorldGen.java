package org.foxesworld.engine.world;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.foxesworld.NewGame;
import org.foxesworld.engine.player.Player;
import org.foxesworld.engine.solarSystem.sun.LightingType;
import org.foxesworld.engine.solarSystem.sun.Sun;
import org.foxesworld.engine.world.skybox.SkyboxGenerator;
import org.foxesworld.engine.world.terrain.TerrainGenerator;
import org.foxesworld.engine.world.water.Water;

import static org.foxesworld.engine.texture.TextureWrap.NONE;

public class WorldGen {

    private int terrainSize;
    private float waterLevel;
    private TerrainGenerator terrain;
    private Water water;
    private Sun sun;
    private SimpleApplication app;

    private Node rootNode;

    public WorldGen(NewGame app, int terrainSize, float waterLevel){
        this.app = app;
        this.rootNode = app.getRootNode();
        this.terrainSize = terrainSize;
        this.waterLevel = waterLevel;
        this.terrain = new TerrainGenerator(app);
        this.water = new Water(app, app.getFpp());
    }

    public void genTerrain(float stepSize, float frequency, int numOctaves, float textureScale, Material mat){
        Node terrain = this.terrain.generateTerrainWithCollision(this.waterLevel, this.terrainSize, stepSize, frequency, numOctaves, textureScale, mat);
        this.rootNode.attachChild(terrain);
    }

    public void genWater(Vector3f light){
        water.generateWater(this.waterLevel, light);
    }

    public void addSky(String texture){
        SkyboxGenerator skyboxGenerator = new SkyboxGenerator(app.getAssetManager());
        Spatial skybox = skyboxGenerator.generateSkybox(texture);
        this.rootNode.attachChild(skybox);
    }

    public void addSun(String name, LightingType type, ColorRGBA color, Vector3f direction, float radius, float brightness){
        sun = new Sun(app, name, type, color, brightness);
        sun.setSunOptions(direction,radius);
        sun.addTexture("DiffuseMap", "assets/textures/planets/sun/sunDiffuse.jpg", NONE);
        sun.setPosition(new Vector3f(0f, 50f, 0f));
        sun.addSun();
    }

    public void addPLayer(NewGame game){
        Player player = new Player(game);
        player.addPlayer(app.getCamera(), terrain.getSpawnPoint());
        game.getBullet().getPhysicsSpace().addAll(player);
        this.rootNode.attachChild(player);
    }

    public Sun getSun(){
        return sun;
    }
}

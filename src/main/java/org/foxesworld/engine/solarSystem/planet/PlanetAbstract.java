package org.foxesworld.engine.solarSystem.planet;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import org.foxesworld.engine.texture.TextureWrap;

public abstract class PlanetAbstract {
    private Node rootNode;

    private BulletAppState bulletAppState;
    private String planetName;
    private Geometry planet;

    public abstract void setGeometry(int zSamples, int radialSamples, float radius);

    public abstract void addPlanet();

    public abstract void addTexture(String mapType, String texture, TextureWrap wrap);
    public Geometry getPlanet() {
        return planet;
    }

    public void setPlanet(Geometry planet){
        this.planet = planet;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }

    public String getPlanetName() {
        return planetName;
    }

    public Node getRootNode() {
        return rootNode;
    }
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }
}

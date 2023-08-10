package org.foxesworld.engine.solarSystem.sun;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import org.foxesworld.engine.texture.LoadTexture;
import org.foxesworld.engine.texture.TextureWrap;

public abstract class SunAbstract {
    private Node rootNode;
    private Geometry sunObject;
    private LoadTexture sunTexture;
    private LightingType lightingType;
    private String planetName;
    private DirectionalLight directionalSunLight;
    private AmbientLight ambientSunLight;
    private PointLight pointSunLight;
    private ColorRGBA color;
    private float brightness;

    public abstract void setSunOptions(Vector3f direction, float radius);

    public abstract void addTexture(String mapType, String texture, TextureWrap wrap);

    public abstract void setPosition(Vector3f pos);

    public abstract void addSun();

    public Node getRootNode() {
        return rootNode;
    }

    public LightingType getLightingType() {
        return lightingType;
    }

    public String getPlanetName() {
        return planetName;
    }

    public DirectionalLight getDirectionalSunLight() {
        return directionalSunLight;
    }

    public AmbientLight getAmbientSunLight() {
        return ambientSunLight;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void setLightingType(LightingType lightingType) {
        this.lightingType = lightingType;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }

    public void setDirectionalSunLight(DirectionalLight directionalSunLight) {
        this.directionalSunLight = directionalSunLight;
    }
    public void setAmbientSunLight(AmbientLight ambientSunLight) {
        this.ambientSunLight = ambientSunLight;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public PointLight getPointSunLight() {
        return pointSunLight;
    }

    public void setPointSunLight(PointLight pointSunLight) {
        this.pointSunLight = pointSunLight;
    }

    public Geometry getSunObject() {
        return sunObject;
    }

    public void setSunObject(Geometry sunObject) {
        this.sunObject = sunObject;
    }

    public LoadTexture getSunTexture() {
        return sunTexture;
    }

    public void setSunTexture(LoadTexture sunTexture) {
        this.sunTexture = sunTexture;
    }
}

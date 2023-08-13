package org.foxesworld.newgame.engine.world.sun;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Sun extends SunAbstract {

    public Sun(AssetManager game, Node rootNode, String name, LightingType type, ColorRGBA color, float brightness) {
        setRootNode(rootNode);
        setPlanetName(name);
        setLightingType(type);
        setColor(color);
        setBrightness(brightness);
    }

    @Override
    public void setSunOptions(Vector3f direction, float radius) {
        Sphere sunSphere = new Sphere(32, 32, radius);
        switch (getLightingType()) {
            case AMBIENT:
                setAmbientSunLight(new AmbientLight());
                getAmbientSunLight().setColor(getColor().mult(getBrightness()));
                break;

            case DIRECTIONAL:
                setDirectionalSunLight(new DirectionalLight());
                getDirectionalSunLight().setDirection(direction.normalizeLocal());
                getDirectionalSunLight().setColor(getColor().mult(getBrightness()));
                break;
            case POINT:
                setPointSunLight(new PointLight());
                getPointSunLight().setColor(getColor().mult(getBrightness()));
                getPointSunLight().setPosition(Vector3f.ZERO);
                getPointSunLight().setRadius(20f);
                break;
        }
        setSunObject(new Geometry(getPlanetName(), sunSphere));
    }

    @Override
    public void addSun(Material mat) {
        getSunObject().setMaterial(mat);
        getRootNode().attachChild(getSunObject());
        switch (getLightingType()) {
            case AMBIENT:
                getRootNode().addLight(getAmbientSunLight());
                break;
            case DIRECTIONAL:
                getRootNode().addLight(getDirectionalSunLight());
                break;
            case POINT:
                getRootNode().addLight(getPointSunLight());
                break;
        }
    }

    @Override
    public void setPosition(Vector3f pos){
        getSunObject().setLocalTranslation(pos);
    }
}

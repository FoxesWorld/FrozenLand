package org.foxesworld.engine.solarSystem.sun;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import org.foxesworld.engine.texture.LoadTexture;
import org.foxesworld.engine.texture.TextureWrap;

public class Sun extends SunAbstract {
    public Sun(SimpleApplication game, String name, LightingType type, ColorRGBA color, float brightness) {
        setSunTexture(new LoadTexture(game, "Common/MatDefs/Light/Lighting.j3md"));
        setRootNode(game.getRootNode());
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
    public void addTexture(String mapType, String texture, TextureWrap wrap){
        getSunTexture().addTexture(mapType, texture, wrap);
    }

    @Override
    public void addSun() {
        getSunObject().setMaterial(getSunTexture().getMaterial());
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

package org.foxesworld.engine.world.water;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class Water {

    private SimpleApplication app;
    private FilterPostProcessor fpp;
    private float waterLevel;

    public Water(SimpleApplication app, FilterPostProcessor fpp){
        this.app = app;
        this.fpp = fpp;
    }

    public void generateWater(float waterLevel, @Deprecated Vector3f light){
            this.waterLevel = waterLevel;
            WaterFilter water = new WaterFilter(app.getRootNode(), light);
            water.setWaterColor(new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f));
            water.setDeepWaterColor(new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f));
            water.setUnderWaterFogDistance(80);
            water.setWaterTransparency(0.12f);
            water.setFoamIntensity(0.4f);
            water.setFoamHardness(0.3f);
            water.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
            water.setReflectionDisplace(50);
            water.setRefractionConstant(0.25f);
            water.setColorExtinction(new Vector3f(30, 50, 70));
            water.setCausticsIntensity(0.4f);
            water.setWaveScale(0.003f);
            water.setMaxAmplitude(2f);
            water.setFoamTexture((Texture2D) app.getAssetManager().loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
            water.setRefractionStrength(0.2f);
            water.setWaterHeight(waterLevel);
            fpp.addFilter(water);
    }

    public float getWaterLevel() {
        return waterLevel;
    }
}

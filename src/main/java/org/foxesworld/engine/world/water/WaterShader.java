package org.foxesworld.engine.world.water;


import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class WaterShader {

    private AssetManager assetManager;
    private FilterPostProcessor fpp;
    private WaterFilter waterFilter;

    public WaterShader(AssetManager assetManager, FilterPostProcessor fpp) {
        this.assetManager = assetManager;
        this.fpp = fpp;
        initializeWaterFilter();
    }

    private void initializeWaterFilter() {
        waterFilter = new WaterFilter();
        waterFilter.setWaveScale(0.003f);
        waterFilter.setMaxAmplitude(2f);
        waterFilter.setFoamIntensity(0.4f);
        waterFilter.setFoamHardness(0.3f);
        waterFilter.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
        waterFilter.setWaterColor(new ColorRGBA(0.0078f, 0.3176f, 0.5f, 1.0f));
        waterFilter.setDeepWaterColor(new ColorRGBA(0.0039f, 0.00196f, 0.145f, 1.0f));
        waterFilter.setUnderWaterFogDistance(80);
        waterFilter.setWaterTransparency(0.12f);
        waterFilter.setReflectionDisplace(50);
        waterFilter.setRefractionConstant(0.25f);
        waterFilter.setColorExtinction(new Vector3f(30, 50, 70));
        waterFilter.setCausticsIntensity(0.4f);
        waterFilter.setWaveScale(0.003f);
        waterFilter.setMaxAmplitude(2f);
        waterFilter.setRefractionStrength(0.2f);

        // Add your water texture here
        Texture foamTexture = assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg");
        waterFilter.setFoamTexture((Texture2D) foamTexture);

        fpp.addFilter(waterFilter);
    }

    public void applyWaterShader(float waterLevel, Vector3f lightDirection) {
        waterFilter.setWaterHeight(waterLevel);
        waterFilter.setLightDirection(lightDirection);
    }

    public WaterFilter getWaterFilter() {
        return waterFilter;
    }
}


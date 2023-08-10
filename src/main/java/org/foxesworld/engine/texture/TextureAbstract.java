package org.foxesworld.engine.texture;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.math.ColorRGBA;

public abstract class TextureAbstract {

    private AssetManager assetManager;
    private Material material;
    private MaterialDef materialDef;

    public abstract void addTexture(String map, String texture, TextureWrap wrap);

    public void setMaterialColor(String map, ColorRGBA color) {
        getMaterial().setColor(map, color);
    }

    public void setMaterialBoolean(String map, boolean val) {
        getMaterial().setBoolean(map, val);
    }

    public void setMaterialFloat(String map, float val) {
        getMaterial().setFloat(map, val);
    }

    public MaterialDef getMaterialDef() {
        return materialDef;
    }

    protected void initMaterial(String matDef) {
        materialDef = (MaterialDef) getAssetManager().loadAsset(matDef);
        material = new Material(getMaterialDef());
    }

    public Material getMaterial() {
        return material;
    }


    public void setDefName(String defName) {
        this.materialDef = (MaterialDef) getAssetManager().loadAsset(defName);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;

    }
}

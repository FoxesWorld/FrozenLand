package org.foxesworld.frozenlands.engine.providers.material;

import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.foxesworld.frozenlands.engine.KernelInterface;

public abstract class MaterialAbstract {
    private KernelInterface kernelInterface;
    private Material material;
    private MaterialDef materialDef;
    public abstract  Material createMat(String dir, String type);
    public abstract void addMaterials();
    public void setMaterialBoolean(String map, boolean val) {
        getMaterial().setBoolean(map, val);
    }

    public void setMaterialVector(String map, String value){
        String[] strArr = value.split(",");
        int[] intArr = new int[3];

        for (int i = 0; i < 3; i++) {
            intArr[i] = Integer.parseInt(strArr[i]);
        }
        Vector3f vector = new Vector3f(intArr[0], intArr[1], intArr[2]);
        getMaterial().setVector3(map, vector);
    }

    public void setMaterialFloat(String map, float val) {
        getMaterial().setFloat(map, val);
    }
    public void setMaterialColor(String map, ColorRGBA color) {
        getMaterial().setColor(map, color);
    }

    public MaterialDef getMaterialDef() {
        return materialDef;
    }
    protected void initMaterial(String matDef) {
        materialDef = (MaterialDef) kernelInterface.getAssetManager().loadAsset(matDef);
        material = new Material(getMaterialDef());
    }
    public KernelInterface getKernelInterface() {
        return kernelInterface;
    }
    public void setAssetManager(KernelInterface kernelInterface) {
        this.kernelInterface = kernelInterface;
    }
    public Material getMaterial() {
        return material;
    }
}

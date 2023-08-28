package org.foxesworld.newgame.engine.providers.material;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import org.foxesworld.newgame.engine.Kernel;
import org.foxesworld.newgame.engine.KernelInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MaterialManager extends MaterialAbstract {

    private String matIndexFile = "materialOptions.json";
    private String matFolder = "";
    private Map<String, Object> matData;
    private Map<String, Material> Materials = new HashMap<>();

    public MaterialManager(KernelInterface kernelInterface) {
        setAssetManager(kernelInterface);
    }
    @Override
    public void addMaterials() {
        getKernelInterface().getLogger().info("Adding materials");
        String[] textures = new String[]{"soil", "sand", "sun", "terrain"};
        for (int c = 0; c < textures.length; c++) {
            String mat = textures[c];
            getKernelInterface().getLogger().info("  - Adding '" + mat + "' material");
            Materials.put(mat, createMat(mat));
        }
    }
    @Override
    public Material createMat(String file) {
        String path = "textures/" + file + "/";
        matData = readMatConfig(MaterialManager.class.getClassLoader().getResourceAsStream(path + matIndexFile));
        initMaterial(String.valueOf(matData.get("matDef")));
        AtomicInteger textNum = new AtomicInteger();
        AtomicInteger varNum = new AtomicInteger();
        handleTextures(path, (mapName, textureInstanceMap) -> {
            TextureWrap wrapType = TextureWrap.valueOf((String) textureInstanceMap.get("wrap"));
            Texture thisTexture = getKernelInterface().getAssetManager().loadTexture(path + "textures/" + textureInstanceMap.get("texture"));
            wrapType(wrapType, thisTexture);
            getMaterial().setTexture(mapName, thisTexture);
            textNum.getAndIncrement();
        });

        handleVars((cfgTitle, value) -> {
            inputType(cfgTitle, value);
            varNum.getAndIncrement();
        });
        getKernelInterface().getLogger().info("    - "+file + " has " + textNum + " textures and " + varNum + " vars");

        return getMaterial();
    }

    private void wrapType(TextureWrap wrapType, Texture thisTexture) {
        switch (wrapType) {
            case REPEAT:
                thisTexture.setWrap(Texture.WrapMode.Repeat);
                break;
            case MIRRORED_REPEAT:
                thisTexture.setWrap(Texture.WrapMode.MirroredRepeat);
                break;
            case EDGE_CLAMP:
                thisTexture.setWrap(Texture.WrapMode.EdgeClamp);
                break;
            case NONE:
                break;
        }
    }

    private void inputType(String cfgTitle, Map<String, Object> value) {
        VarType inputType = VarType.valueOf(((String) value.get("type")).toUpperCase());
        switch (inputType) {
            case FLOAT:
                setMaterialFloat(cfgTitle, (Integer) value.get("value"));
                break;
            case BOOLEAN:
                setMaterialBoolean(cfgTitle, (Boolean) value.get("value"));
                break;
            case COLOR:
                setMaterialColor(cfgTitle, parseColor((String) value.get("value")));
                break;
            case VECTOR:
                setMaterialVector(cfgTitle, (String) value.get("value"));
                break;
        }
    }

    private void handleTextures(String path, BiConsumer<String, Map<String, Object>> consumer) {
        LinkedHashMap<String, Map<String, Object>> texturesMap = (LinkedHashMap<String, Map<String, Object>>) matData.get("textures");
        texturesMap.forEach((mapName, textureInstanceMap) -> {
            //TextureWrap wrapType = TextureWrap.valueOf((String) textureInstanceMap.get("wrap"));
            //Texture thisTexture = getAssetManager().loadTexture(path + "/" + textureInstanceMap.get("texture"));
            //wrapType(wrapType, thisTexture);
            //System.out.println("Setting WrapType " + wrapType + " of " + thisTexture.getName());

            consumer.accept(mapName, textureInstanceMap);
        });
    }

    private void handleVars(BiConsumer<String, Map<String, Object>> consumer) {
        LinkedHashMap<String, Map<String, Object>> varsMap = (LinkedHashMap<String, Map<String, Object>>) matData.get("vars");
        varsMap.forEach((cfgTitle, value) -> {
            inputType(cfgTitle, value);
            consumer.accept(cfgTitle, value);
        });
    }

    private ColorRGBA parseColor(String colorStr) {
        String[] rgba = colorStr.split(",");
        float r = Float.parseFloat(rgba[0]);
        float g = Float.parseFloat(rgba[1]);
        float b = Float.parseFloat(rgba[2]);
        float a = Float.parseFloat(rgba[3]);
        return new ColorRGBA(r, g, b, a);
    }

    private enum VarType {
        FLOAT,
        VECTOR,
        BOOLEAN,
        COLOR
    }

    private Map<String, Object> readMatConfig(InputStream is) {
        Map<String, Object> map = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
            };
            map = mapper.readValue(is, typeRef);
        } catch (IOException ignored) {
            System.out.println(ignored);
        }
        return map;
    }

    @FunctionalInterface
    private interface BiConsumer<T, U> {
        void accept(T t, U u);
    }

    public Material getMaterial(String mat) {
        return Materials.get(mat);
    }

    public Map<String, Material> getMaterials() {
        return Materials;
    }

    public void addMatData(String optName, Object optCfg) {
        this.matData.put(optName, optCfg);
    }

    public void setMatIndexFile(String matIndexFile) {
        this.matIndexFile = matIndexFile;
    }

    public void setMatFolder(String matFolder) {
        this.matFolder = matFolder;
    }
}

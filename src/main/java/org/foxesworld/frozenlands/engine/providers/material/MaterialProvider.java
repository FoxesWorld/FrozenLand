package org.foxesworld.frozenlands.engine.providers.material;

import com.google.gson.Gson;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.KernelInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.foxesworld.frozenlands.engine.utils.Utils.inputJsonReader;

public class MaterialProvider extends MaterialAbstract {
    private final Map<String, Material> materials = new HashMap<>();

    public MaterialProvider(KernelInterface kernelInterface) {
        setAssetManager(kernelInterface);
    }

    @Override
    public void loadMaterials(String path) {
        FrozenLands.logger.info("Adding materials");
        String materialsJson = inputJsonReader(getKernelInterface(), path);

        MaterialData materialData = new Gson().fromJson(materialsJson, MaterialData.class);

        // Чтение текстур
        List<TextureData> textures = materialData.getTextures();
        textures.forEach(textureData -> {
            String regName = textureData.getRegName();
            TextureOptions regOptions = textureData.getRegOptions();
            String texturePath = regOptions.getTexture();
            String wrap = regOptions.getWrap();
            // Создание материала с текстурой
            createMat(texturePath, wrap, regName);
        });

        // Чтение переменных
        List<VarData> vars = materialData.getVars();
        vars.forEach(varData -> {
            String varName = varData.getVarName();
            VarOptions varOpt = varData.getVarOpt();
            String type = varOpt.getType();
            String value = varOpt.getValue();
            // Создание материала с переменной
            createMat(varName, type, value);
        });

        FrozenLands.logger.info("Finished adding materials, total matAmount: " + materials.size());
    }



    @Override
    public Material createMat(String dir, String type, String regName) {
        String baseDir = "textures/" + dir + '/';
        MatOpt matData = readMatConfig(baseDir + "matOpt/" + type + ".json");
        initMaterial(matData.getMatDef());
        AtomicInteger textNum = new AtomicInteger();
        AtomicInteger varNum = new AtomicInteger();

        handleTextures(matData, (mapName, textureInstance) -> {
            TextureWrap wrapType = TextureWrap.valueOf(textureInstance.getWrap());
            Texture thisTexture = getKernelInterface().getAssetManager().loadTexture(baseDir + "textures/" + textureInstance.getTexture());
            wrapType(wrapType, thisTexture);
            if (textureInstance.getTextureScale() != null) {
                String[] scaleComponents = textureInstance.getTextureScale().split(",");
                thisTexture.setWrap(Texture.WrapMode.Repeat);
                thisTexture.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
                thisTexture.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
                thisTexture.setAnisotropicFilter(4);
                thisTexture.setMinFilter(Texture.MinFilter.Trilinear);
                thisTexture.setMagFilter(Texture.MagFilter.Bilinear);
                //thisTexture.setTexCoordOffset(0, new Vector2f(0, 0));
                //thisTexture.setTexCoordScale(0, new Vector2f(Float.parseFloat(scaleComponents[0]), Float.parseFloat(scaleComponents[1])));
            }
            getMaterial().setTexture(regName, thisTexture);
            textNum.getAndIncrement();
        });

        handleVars(matData, (cfgTitle, value) -> {
            inputType(cfgTitle, value);
            varNum.getAndIncrement();
        });

        FrozenLands.logger.info("    - " + dir + '#' + type + " has " + textNum + " textures and " + varNum + " vars");

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

    private void inputType(String cfgTitle, MaterialValue value) {
        VarType inputType = VarType.valueOf(value.getType().toUpperCase());
        switch (inputType) {
            case FLOAT:
                setMaterialFloat(cfgTitle, value.getFloatValue());
                break;
            case BOOLEAN:
                setMaterialBoolean(cfgTitle, value.getBooleanValue());
                break;
            case COLOR:
                setMaterialColor(cfgTitle, parseColor(value.getColorValue()));
                break;
            case VECTOR:
                setMaterialVector(cfgTitle, value.getVectorValue());
                break;
        }
    }


    private void handleTextures(MatOpt matData, BiConsumer<String, TextureInstance> consumer) {
        Map<String, TextureInstance> texturesMap = matData.getTextures();
        texturesMap.forEach(consumer::accept);
    }

    private void handleVars(MatOpt matData, BiConsumer<String, MaterialValue> consumer) {
        Map<String, Map<String, Object>> varsMap = matData.getVars();
        varsMap.forEach((cfgTitle, value) -> {
            MaterialValue materialValue = new MaterialValue((String) value.get("type"), (float) value.get("floatValue"), (boolean) value.get("booleanValue"), (String) value.get("colorValue"), (String) value.get("vectorValue"));
            consumer.accept(cfgTitle, materialValue);
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

    private MatOpt readMatConfig(String is) {
        return new Gson().fromJson(inputJsonReader(getKernelInterface(), is), MatOpt.class);
    }

    static class MatOpt {
        private String matDef;
        private Map<String, TextureInstance> textures;
        private Map<String, Map<String, Object>> vars;

        public String getMatDef() {
            return matDef;
        }

        public Map<String, TextureInstance> getTextures() {
            return textures;
        }

        public Map<String, Map<String, Object>> getVars() {
            return vars;
        }
    }

    static class TextureInstance {
        private String texture;
        private String wrap;
        private String textureScale;

        public String getTexture() {
            return texture;
        }

        public String getWrap() {
            return wrap;
        }

        public String getTextureScale() {
            return textureScale;
        }
    }

    private enum VarType {
        FLOAT,
        VECTOR,
        BOOLEAN,
        COLOR
    }

    public class MaterialValue {
        private String type;
        private float floatValue;
        private boolean booleanValue;
        private String colorValue;
        private String vectorValue;

        public MaterialValue(String type, float floatValue, boolean booleanValue, String colorValue, String vectorValue) {
            this.type = type;
            this.floatValue = floatValue;
            this.booleanValue = booleanValue;
            this.colorValue = colorValue;
            this.vectorValue = vectorValue;
        }

        public String getType() {
            return type;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public boolean getBooleanValue() {
            return booleanValue;
        }

        public String getColorValue() {
            return colorValue;
        }

        public Vector3f getVectorValue() {
            String[] components = vectorValue.split(",");
            float x = Float.parseFloat(components[0]);
            float y = Float.parseFloat(components[1]);
            float z = Float.parseFloat(components[2]);
            return new Vector3f(x, y, z);
        }
    }

    @FunctionalInterface
    private interface BiConsumer<T, U> {
        void accept(T t, U u);
    }

    public Material getMaterial(String mat) {
        return materials.get(mat);
    }
}

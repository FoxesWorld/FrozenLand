package org.foxesworld.frozenlands.engine.providers.material;

import com.google.gson.Gson;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.Kernel;
import org.foxesworld.frozenlands.engine.providers.material.attributes.*;

import java.util.HashMap;
import java.util.Map;

import static org.foxesworld.frozenlands.engine.utils.Utils.inputJsonReader;

public class MaterialProvider extends MaterialAbstract {
    private final Map<String, Material> materials = new HashMap<>();
private final  Kernel kernel;
    public MaterialProvider(Kernel kernel) {
        this.kernel = kernel;
        setAssetManager(kernel);
    }

    @Override
    public void loadMaterials(String path) {
        FrozenLands.logger.info("Adding materials");
        for (Materials mat: new Gson().fromJson(inputJsonReader(getKernel(), path), Materials[].class)) {
            FrozenLands.logger.info("  - Adding '" + mat.getMatName() + "' material of type " + mat.getMatType());
            materials.put(mat.getMatName() + '#' + mat.getMatType(), createMat(mat.getMatName(), mat.getMatType()));
        }

        FrozenLands.logger.info("Finished adding materials, total matAmount: " + materials.size());
    }

    @Override
    public Material createMat(String dir, String type) {
        int textureNum = 0, varNum = 0;
        String baseDir = "textures/" + dir + '/';
        MatOpt matOpt = readMatConfig(baseDir + "matOpt/" + type + ".json");
        initMaterial(matOpt.getMatDef());
        getMaterial().setName(dir);
        for(TextureInstance textureInstance: matOpt.getTextures()){
            //TextureWrap wrapType = TextureWrap.valueOf(textureInstance.getRegOptions().getWrap());
            Texture thisTexture = getKernel().getAssetManager().loadTexture(baseDir + "textures/" + textureInstance.getRegOptions().getTexture());
            thisTexture.setWrap(Texture.WrapMode.valueOf(textureInstance.getRegOptions().getWrap()));
            //wrapType(wrapType, thisTexture);
            // TODO
            // Image Size can be set here
            try {
                getMaterial().setTexture(textureInstance.getRegName(), thisTexture);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            textureNum++;
        }

        for (VarData varOption: matOpt.getVars()) {
            inputType(varOption.getVarName(), varOption.getVarOpt());
            varNum++;
        }
        FrozenLands.logger.info("    - "+ dir + '#'+type + " has " + textureNum + " textures and " + varNum + " vars");

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

    private void inputType(String cfgTitle,VarOptions value) {
        VarType inputType = VarType.valueOf(value.getType().toUpperCase());
        switch (inputType) {
            case FLOAT -> setMaterialFloat(cfgTitle, Integer.parseInt((String) value.getValue()));
            case BOOLEAN -> setMaterialBoolean(cfgTitle, (Boolean) value.getValue());
            case COLOR -> setMaterialColor(cfgTitle, parseColor((String) value.getValue()));
            case VECTOR -> {
                String[] valStr = String.valueOf(value.getValue()).split(",");
                Vector3f vector3f = new Vector3f(Integer.parseInt(valStr[0]), Integer.parseInt(valStr[1]), Integer.parseInt(valStr[2]));
                setMaterialVector(cfgTitle, vector3f);
            }
        }
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
        return new Gson().fromJson(inputJsonReader(getKernel(), is), MatOpt.class);
    }

    private enum VarType {
        FLOAT,
        VECTOR,
        BOOLEAN,
        COLOR
    }

    public Material getMaterial(String mat) {
        return materials.get(mat);
    }
}

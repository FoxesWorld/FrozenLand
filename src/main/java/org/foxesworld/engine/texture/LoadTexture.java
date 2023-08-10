package org.foxesworld.engine.texture;

import com.jme3.app.SimpleApplication;
import com.jme3.texture.Texture;

import java.util.List;
import java.util.Map;

public class LoadTexture extends TextureAbstract {

    public LoadTexture(SimpleApplication app, String matDef) {
        setAssetManager(app.getAssetManager());
        initMaterial(matDef);
    }

    public void addTextures(List<Map<TextureWrap, Map<String, String>>> textures){
        for(int j = 0; j<textures.size(); j++){
            Map<TextureWrap, Map<String, String>> textureSet = textures.get(j);
            for(Map.Entry<TextureWrap, Map<String, String>> textureDefinitions: textureSet.entrySet()){
                for(Map.Entry<String, String> textStr: textureDefinitions.getValue().entrySet()){
                    addTexture(textStr.getKey(), textStr.getValue(), textureDefinitions.getKey());
                }
            }
        }
    }
    @Override
    public void addTexture(String map, String texture, TextureWrap wrap) {
       Texture thisTexture = getAssetManager().loadTexture(texture);
        switch (wrap) {
            case REPEAT:
                thisTexture.setWrap(Texture.WrapMode.Repeat);
                break;

            case MIRRORED_REPEAT:
                thisTexture.setWrap(Texture.WrapMode.MirroredRepeat);
                break;

            case EDGE_CLAMP:
                thisTexture.setWrap(Texture.WrapMode.EdgeClamp);
                break;
            default:
                break;
        }
        getMaterial().setTexture(map, thisTexture);
    }
}

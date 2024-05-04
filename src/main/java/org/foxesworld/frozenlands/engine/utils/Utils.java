package org.foxesworld.frozenlands.engine.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import org.foxesworld.frozenlands.engine.KernelInterface;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.SplittableRandom;

public class Utils {
    private static SplittableRandom random = new SplittableRandom();
    private Utils() {}
    public static boolean getRandom(int probability) {
        return random.nextInt(1, 101) <= probability;
    }
    public static float getRandomNumber() {
        return random.nextInt(0, 100);
    }
    public static float getRandomNumberInRange(float min, float max) { return (float) random.doubles(min, max).findAny().getAsDouble();}
    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public static JsonNode inputJsonReader(KernelInterface kernelInterface, String path){
        ObjectMapper objectMapper = new ObjectMapper();
        boolean inputSearch = (boolean) kernelInterface.getConfig().get("engine").get("assetsInputStream");
        if(inputSearch) {
            InputStream is = kernelInterface.getAssetManager().locateAsset(new AssetKey<>(path)).openStream();
            try {
                return objectMapper.readTree(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                return  objectMapper.readTree(new File(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
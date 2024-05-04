package org.foxesworld.frozenlands.engine.utils;

import org.foxesworld.frozenlands.engine.KernelInterface;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public static String inputJsonReader(KernelInterface kernelInterface, String path) {
        if (kernelInterface == null || kernelInterface.getConfig() == null) {
            return null;
        }

        boolean inputSearch = (boolean) kernelInterface.getConfig().get("internal/engine").get("assetsInputStream");
        if (inputSearch) {
            try (InputStreamReader is = new InputStreamReader(kernelInterface.getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(is)) {
                StringBuilder jsonStringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }

                return jsonStringBuilder.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                StringBuilder jsonStringBuilder = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    jsonStringBuilder.append(line);
                }

                return jsonStringBuilder.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
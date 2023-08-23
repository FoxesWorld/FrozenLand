package org.foxesworld.newgame.engine.utils;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import org.foxesworld.newgame.engine.player.PlayerInterface;

import java.util.SplittableRandom;

public class Utils {
    private static SplittableRandom random = new SplittableRandom();

    private Utils() {
    }

    public static boolean getRandom(int probability) {
        return random.nextInt(1, 101) <= probability;
    }

    public static float getRandomNumber() {
        return random.nextInt(0, 100);
    }

    public static float getRandomNumberInRange(float min, float max) {
        return (float) random.doubles(min, max).findAny().getAsDouble();
    }


    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
}
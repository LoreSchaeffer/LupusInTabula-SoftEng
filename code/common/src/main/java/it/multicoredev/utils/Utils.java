package it.multicoredev.utils;

import java.util.Random;

public class Utils {
    private static Random random = new Random();

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (Static.DEBUG) LitLogger.get().error("Sleep interrupted", e);
        }
    }

    public static int randInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}

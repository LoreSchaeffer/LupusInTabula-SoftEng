package it.multicoredev.utils;

public class Utils {

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            if (Static.DEBUG) LitLogger.get().error("Sleep interrupted", e);
        }
    }
}

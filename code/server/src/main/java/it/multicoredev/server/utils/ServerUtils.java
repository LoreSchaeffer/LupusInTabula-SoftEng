package it.multicoredev.server.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServerUtils {
    private static final String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
    private static final Random rand = new Random();

    public static String createRandomCode(int len) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < len; i++) {
            builder.append(chars.charAt(rand.nextInt(chars.length())));
        }

        return builder.toString();
    }

    public static <K> Map<K, Integer> counter(List<K> list) {
        Map<K, Integer> map = new HashMap<>();
        list.forEach(k -> {
            if (map.containsKey(k)) map.put(k, map.get(k) + 1);
            else map.put(k, 1);
        });

        return map;
    }

    public static <K> K getMostFrequent(Map<K, Integer> map) {
        K mostFrequent = null;
        int max = 0;

        for (Map.Entry<K, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                mostFrequent = entry.getKey();
                max = entry.getValue();
            }
        }

        return mostFrequent;
    }
}

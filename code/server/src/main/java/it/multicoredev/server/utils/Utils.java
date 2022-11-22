package it.multicoredev.server.utils;

import java.util.Random;

public class Utils {
    private static final String chars = "abcdefghilmnopqrstuvzxkjy0123456789";
    private static final Random rand = new Random();

    public static String randomCode(int len){
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < len; i++){
            builder.append(chars.charAt(rand.nextInt(chars.length())));
        }

        return builder.toString();
    }

}

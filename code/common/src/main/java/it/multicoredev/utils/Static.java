package it.multicoredev.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.multicoredev.adapters.UUIDAdapter;
import it.multicoredev.mclib.network.PacketByteBuf;

import java.util.UUID;

public class Static {
    public static boolean DEBUG = false;
    public static Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(UUID.class, new UUIDAdapter())
            .create();

    static {
        PacketByteBuf.setGson(GSON);
    }
}

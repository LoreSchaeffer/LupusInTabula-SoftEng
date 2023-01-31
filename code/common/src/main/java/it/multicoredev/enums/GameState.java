package it.multicoredev.enums;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(GameState.Adapter.class)
public enum GameState {
    WAITING,
    STARTING,
    RUNNING,
    ENDING,
    STOPPED;

    public static class Adapter implements JsonSerializer<GameState>, JsonDeserializer<GameState> {

        @Override
        public GameState deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonPrimitive()) throw new JsonParseException("Expected an int");
            return GameState.values()[json.getAsInt()];
        }

        @Override
        public JsonElement serialize(GameState gameState, Type t, JsonSerializationContext ctx) {
            return new JsonPrimitive(gameState.ordinal());
        }
    }
}

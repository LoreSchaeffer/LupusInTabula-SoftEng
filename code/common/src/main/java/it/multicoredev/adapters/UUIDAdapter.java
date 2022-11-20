package it.multicoredev.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
        if (!json.isJsonPrimitive()) throw new JsonParseException("Expected a string");
        return UUID.fromString(json.getAsString());
    }

    @Override
    public JsonElement serialize(UUID src, Type t, JsonSerializationContext ctx) {
        return new JsonPrimitive(src.toString());
    }
}

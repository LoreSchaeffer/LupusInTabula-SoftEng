package it.multicoredev.client.assets;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.mclib.json.JsonConfig;
import it.multicoredev.text.ILocalization;
import it.multicoredev.text.Text;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@JsonAdapter(Locale.Adapter.class)
public class Locale extends JsonConfig implements ILocalization {
    private Map<String, String> texts;

    @Override
    public Locale init() {
        if (texts == null) texts = new HashMap<>();

        for (Text text : Text.values()) {
            if (!texts.containsKey(text.getPath())) {
                texts.put(text.getPath(), text.getDefValue());
            }
        }

        return this;
    }

    @Override
    public String get(String path) {
        return texts.get(path);
    }

    public static class Adapter implements JsonSerializer<Locale>, JsonDeserializer<Locale> {

        @Override
        public Locale deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Locale must be an object");

            JsonObject obj = json.getAsJsonObject();

            Locale locale = new Locale();
            locale.texts = new HashMap<>();

            obj.asMap().forEach((k, v) -> {
                if (!v.isJsonPrimitive()) throw new JsonParseException("Locale values must be primitives");
                locale.texts.put(k, v.getAsString());
            });

            return locale;
        }

        @Override
        public JsonElement serialize(Locale locale, Type type, JsonSerializationContext ctx) {
            JsonObject obj = new JsonObject();
            locale.texts.forEach(obj::addProperty);
            return obj;
        }
    }
}

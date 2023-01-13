package it.multicoredev.text;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(BaseText.Adapter.class)
public abstract class BaseText {
    protected final String type;

    public BaseText(String type) {
        this.type = type;
    }

    public abstract String getText();

    public static class Adapter implements JsonSerializer<BaseText>, JsonDeserializer<BaseText> {

        @Override
        public BaseText deserialize(JsonElement jsonElement, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("Expected object, got " + jsonElement);
            JsonObject obj = jsonElement.getAsJsonObject();

            if (!obj.has("type")) throw new JsonParseException("Missing type");
            String type = obj.get("type").getAsString();

            if (type.equals("static")) return ctx.deserialize(obj, StaticText.class);
            else if (type.equals("translatable")) return ctx.deserialize(obj, TranslatableText.class);
            else throw new JsonParseException("Unknown type " + type);
        }

        @Override
        public JsonElement serialize(BaseText baseText, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(baseText, baseText.getClass());
        }
    }
}

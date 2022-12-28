package it.multicoredev.client.ui.comms;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import org.cef.callback.CefQueryCallback;

import java.lang.reflect.Type;

@JsonAdapter(InternalMessage.Adapter.class)
public abstract class InternalMessage {
    private final String type;

    public InternalMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback);

    public static class Adapter implements JsonDeserializer<InternalMessage> {

        @Override
        public InternalMessage deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Invalid message");

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("type")) throw new JsonParseException("Invalid message");

            String typeId = obj.get("type").getAsString();
            Class<? extends InternalMessage> type = MessageRegistry.getType(typeId);
            if (type == null) throw new JsonParseException("Invalid message type");

            return ctx.deserialize(json, type);
        }
    }
}

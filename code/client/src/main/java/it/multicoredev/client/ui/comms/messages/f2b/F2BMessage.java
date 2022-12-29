package it.multicoredev.client.ui.comms.messages.f2b;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.comms.F2BMessageRegistry;
import org.cef.callback.CefQueryCallback;

import java.lang.reflect.Type;

@JsonAdapter(F2BMessage.Adapter.class)
public abstract class F2BMessage {
    private final String type;

    public F2BMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback);

    public static class Adapter implements JsonDeserializer<F2BMessage> {

        @Override
        public F2BMessage deserialize(JsonElement json, Type t, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("Invalid message");

            JsonObject obj = json.getAsJsonObject();
            if (!obj.has("type")) throw new JsonParseException("Invalid message");

            String typeId = obj.get("type").getAsString();
            Class<? extends F2BMessage> type = F2BMessageRegistry.getType(typeId);
            if (type == null) throw new JsonParseException("Invalid message type");

            return ctx.deserialize(json, type);
        }
    }
}

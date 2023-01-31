package it.multicoredev.server.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Client;
import it.multicoredev.models.Player;
import it.multicoredev.server.network.ServerNetHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

@JsonAdapter(ServerPlayer.Adapter.class)
public class ServerPlayer extends Player {
    private ServerNetHandler netHandler;

    // ServerNetHandler is null only for fake players
    public ServerPlayer(@NotNull UUID uuid, @NotNull String name, boolean gameMaster, ServerNetHandler netHandler) {
        super(uuid, name, gameMaster);

        this.netHandler = netHandler;
    }

    public ServerPlayer(@NotNull Client client, boolean gameMaster, @NotNull ServerNetHandler netHandler) {
        super(client, gameMaster);

        this.netHandler = netHandler;
    }

    public void sendPacket(@NotNull Packet<?> packet) throws PacketSendException {
        if (netHandler == null) return;
        netHandler.sendPacket(packet);
    }

    public boolean isConnected() {
        if (netHandler == null) return false;
        return netHandler.isConnected();
    }

    public void disconnect() {
        netHandler.disconnect();
    }

    public void setDisconnected() {
        netHandler = null;
    }

    public static class Adapter implements JsonSerializer<ServerPlayer> {

        @Override
        public JsonElement serialize(ServerPlayer player, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(player, Player.class);
        }
    }
}

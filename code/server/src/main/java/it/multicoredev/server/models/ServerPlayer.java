package it.multicoredev.server.models;

import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Client;
import it.multicoredev.models.Player;
import it.multicoredev.server.network.ServerNetHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerPlayer extends Player {
    private final ServerNetHandler netHandler;

    public ServerPlayer(@NotNull UUID uuid, @NotNull String name, boolean gameMaster, @NotNull ServerNetHandler netHandler) {
        super(uuid, name, gameMaster);

        this.netHandler = netHandler;
    }

    public ServerPlayer(@NotNull Client client, boolean gameMaster, @NotNull ServerNetHandler netHandler) {
        super(client, gameMaster);

        this.netHandler = netHandler;
    }

    public void sendPacket(@NotNull Packet<?> packet) throws PacketSendException {
        netHandler.sendPacket(packet);
    }

    public boolean isConnected() {
        return netHandler.isConnected();
    }

    public void disconnect() {
        netHandler.disconnect();
    }
}

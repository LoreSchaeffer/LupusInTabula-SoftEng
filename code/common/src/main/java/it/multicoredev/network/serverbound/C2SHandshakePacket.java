package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IServerPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class C2SHandshakePacket implements Packet<IServerPacketListener> {
    private UUID clientId;
    private String username;

    public C2SHandshakePacket(@NotNull UUID clientId, @NotNull String username) {
        this.clientId = clientId;
        this.username = username;
    }

    public C2SHandshakePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (clientId == null || username == null || username.trim().isEmpty()) throw new EncoderException("Invalid packet data");

        buf.writeObject(clientId);
        buf.writeString(username);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        clientId = buf.readObject(UUID.class);
        username = buf.readString();

        if (clientId == null || username == null || username.trim().isEmpty()) throw new DecoderException("Invalid packet data");
    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleHandshake(this);
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }
}

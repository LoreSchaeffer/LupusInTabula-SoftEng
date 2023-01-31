package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.utils.Encryption;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class S2CHandshakePacket implements Packet<IClientPacketListener> {
    private boolean clientAccepted;
    private String secret;
    private String reason;
    private UUID newClientId;

    public S2CHandshakePacket(boolean clientAccepted, @Nullable String reason, @Nullable UUID newClientId) {
        this.clientAccepted = clientAccepted;
        this.secret = Encryption.getSecret();
        this.reason = reason;
        this.newClientId = newClientId;
    }

    public S2CHandshakePacket(boolean clientAccepted, @Nullable String reason) {
        this.clientAccepted = clientAccepted;
        this.secret = Encryption.getSecret();
        this.reason = reason;
    }

    public S2CHandshakePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        buf.writeBoolean(clientAccepted);
        buf.writeString(secret);
        buf.writeBoolean(reason != null);
        buf.writeBoolean(newClientId != null);
        if (reason != null) buf.writeString(reason);
        if (newClientId != null) buf.writeObject(newClientId);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        clientAccepted = buf.readBoolean();
        secret = buf.readString();

        boolean hasReason = buf.readBoolean();
        boolean hasChangeClientId = buf.readBoolean();

        if (hasReason) reason = buf.readString();
        else reason = null;

        if (hasChangeClientId) newClientId = buf.readObject(UUID.class);
        else newClientId = null;
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleHandshake(this);
    }

    public boolean isClientAccepted() {
        return clientAccepted;
    }

    public String getSecret() {
        return secret;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    @Nullable
    public UUID getNewClientId() {
        return newClientId;
    }
}

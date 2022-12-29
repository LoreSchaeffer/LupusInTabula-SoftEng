package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class S2CPlayerLeavePacket implements Packet<IClientPacketListener> {
    private UUID clientId;
    private boolean readyToStart;

    public S2CPlayerLeavePacket(@NotNull UUID clientId, boolean readyToStart) {
        this.clientId = clientId;
        this.readyToStart = readyToStart;
    }

    public S2CPlayerLeavePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (clientId == null) throw new EncoderException("clientId is null");

        buf.writeString(clientId.toString());
        buf.writeBoolean(readyToStart);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        String str = buf.readString();

        if (str == null) throw new DecoderException("clientId is null");

        clientId = UUID.fromString(str);
        readyToStart = buf.readBoolean();
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handlePlayerLeave(this);
    }

    public UUID getClientId() {
        return clientId;
    }

    public boolean isReadyToStart() {
        return readyToStart;
    }
}

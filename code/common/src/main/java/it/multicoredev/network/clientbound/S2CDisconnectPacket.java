package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.DisconnectReason;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CDisconnectPacket implements Packet<IClientPacketListener> {
    private DisconnectReason reason;

    public S2CDisconnectPacket(@NotNull DisconnectReason reason) {
        this.reason = reason;
    }

    public S2CDisconnectPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (reason == null) throw new EncoderException("Reason cannot be null");

        buf.writeInt(reason.ordinal());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        reason = DisconnectReason.fromOrdinal(buf.readInt());

        if (reason == null) throw new DecoderException("Reason cannot be null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleDisconnect(this);
    }
}

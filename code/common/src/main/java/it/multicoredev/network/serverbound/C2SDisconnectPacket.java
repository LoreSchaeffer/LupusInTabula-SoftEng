package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.DisconnectReason;
import it.multicoredev.network.IServerPacketListener;
import org.jetbrains.annotations.NotNull;

public class C2SDisconnectPacket implements Packet<IServerPacketListener> {
    private DisconnectReason reason;

    public C2SDisconnectPacket(@NotNull DisconnectReason reason) {
        this.reason = reason;
    }

    public C2SDisconnectPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (reason == null) throw new EncoderException("Reason cannot be null");

        buf.writeInt(reason.ordinal());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        reason = DisconnectReason.values()[buf.readInt()];

        if (reason == null) throw new DecoderException("Reason cannot be null");
    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleDisconnect(this);
    }
}

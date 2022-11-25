package it.multicoredev.network.clientbound;

import it.multicoredev.enums.Message;
import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CAlertPacket implements Packet<IClientPacketListener> {
    private Message message;

    public S2CAlertPacket(@NotNull Message message) {
        this.message = message;
    }

    public S2CAlertPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (message == null) throw new EncoderException("Message cannot be null");

        buf.writeInt(message.ordinal());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        message = Message.values()[buf.readInt()];

        if (message == null) throw new DecoderException("Message cannot be null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleAlert(this);
    }
}

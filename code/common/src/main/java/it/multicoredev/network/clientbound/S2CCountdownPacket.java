package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;

public class S2CCountdownPacket implements Packet<IClientPacketListener> {
    private int number;

    public S2CCountdownPacket(int number) {
        this.number = number;
    }

    public S2CCountdownPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        buf.writeInt(number);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        number = buf.readInt();
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleCountdown(this);
    }
}

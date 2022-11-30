package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;

public class S2CTimePacket implements Packet<IClientPacketListener> {
    private boolean incrementDay;
    private boolean night;

    public S2CTimePacket(boolean incrementDay, boolean night) {
        this.incrementDay = incrementDay;
        this.night = night;
    }

    public S2CTimePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        buf.writeBoolean(incrementDay);
        buf.writeBoolean(night);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        incrementDay = buf.readBoolean();
        night = buf.readBoolean();
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleTime(this);
    }
}

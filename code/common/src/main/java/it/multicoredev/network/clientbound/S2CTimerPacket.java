package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;

public class S2CTimerPacket implements Packet<IClientPacketListener> {
    private int time;

    public S2CTimerPacket(int time) {
        this.time = time;
    }

    public S2CTimerPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        buf.writeInt(time);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        time = buf.readInt();
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleTimer(this);
    }

    public int getTime() {
        return time;
    }
}

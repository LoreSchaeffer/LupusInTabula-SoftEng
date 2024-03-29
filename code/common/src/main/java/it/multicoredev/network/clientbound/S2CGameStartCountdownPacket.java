package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;

public class S2CGameStartCountdownPacket implements Packet<IClientPacketListener> {
    private int seconds;

    public S2CGameStartCountdownPacket(int seconds) {
        this.seconds = seconds;
    }

    public S2CGameStartCountdownPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        buf.writeInt(seconds);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        seconds = buf.readInt();
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleCountdown(this);
    }

    public int getSeconds() {
        return seconds;
    }
}

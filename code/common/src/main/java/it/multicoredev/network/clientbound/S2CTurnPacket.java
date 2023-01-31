package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;

public class S2CTurnPacket implements Packet<IClientPacketListener> {
    public static final S2CTurnPacket START = new S2CTurnPacket(true);
    public static final S2CTurnPacket END = new S2CTurnPacket(false);

    private boolean turnStart;

    public S2CTurnPacket(boolean turnStart) {
        this.turnStart = turnStart;
    }

    public S2CTurnPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        buf.writeBoolean(turnStart);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        turnStart = buf.readBoolean();
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleTurn(this);
    }

    public boolean isTurnStart() {
        return turnStart;
    }
}

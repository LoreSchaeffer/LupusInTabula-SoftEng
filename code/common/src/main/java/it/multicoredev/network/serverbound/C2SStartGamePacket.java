package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IServerPacketListener;

public class C2SStartGamePacket implements Packet<IServerPacketListener> {

    public C2SStartGamePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {

    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {

    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleStartGame(this);
    }
}

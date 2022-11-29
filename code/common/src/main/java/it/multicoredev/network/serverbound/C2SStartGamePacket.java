package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IServerPacketListener;
import org.jetbrains.annotations.NotNull;

public class C2SStartGamePacket implements Packet<IServerPacketListener> {
    private String code;
    //TODO Remove code (not needed)

    public C2SStartGamePacket(@NotNull String code) {
        this.code = code;
    }

    public C2SStartGamePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (code == null || code.trim().isEmpty()) throw new EncoderException("Code cannot be null or empty");

        buf.writeString(code);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        code = buf.readString();

        if (code == null || code.trim().isEmpty()) throw new DecoderException("Code cannot be null or empty");
    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleStartGame(this);
    }

    public String getCode() {
        return code;
    }
}

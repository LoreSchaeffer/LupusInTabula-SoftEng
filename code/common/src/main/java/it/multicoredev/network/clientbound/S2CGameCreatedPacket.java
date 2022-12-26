package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CGameCreatedPacket implements Packet<IClientPacketListener> {
    private String code;

    public S2CGameCreatedPacket(@NotNull String code) {
        this.code = code;
    }

    public S2CGameCreatedPacket() {
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
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleGameCreated(this);
    }

    public String getCode() {
        return code;
    }
}

package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IServerPacketListener;
import org.jetbrains.annotations.NotNull;

public class C2SMessagePacket implements Packet<IServerPacketListener> {
    private String message;

    public C2SMessagePacket(@NotNull String message) {
        this.message = message;
    }

    public C2SMessagePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (message == null || message.trim().isEmpty()) throw new EncoderException("Message cannot be null or empty");
        buf.writeString(message);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        message = buf.readString();

        if (message == null || message.trim().isEmpty()) throw new DecoderException("Message cannot be null or empty");
    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleMessage(this);
    }

    public String getMessage() {
        return message;
    }
}

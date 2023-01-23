package it.multicoredev.network.serverbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IServerPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class C2SSelectPacket implements Packet<IServerPacketListener> {
    private UUID uuid;

    public C2SSelectPacket(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    public C2SSelectPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (uuid == null) throw new EncoderException("UUID is null");

        buf.writeString(uuid.toString());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        String uuidString = buf.readString();

        try {
            uuid = UUID.fromString(uuidString);
        } catch (Exception e) {
            throw new DecoderException("Invalid UUID: " + uuidString);
        }
    }

    @Override
    public void processPacket(IServerPacketListener handler) throws ProcessException {
        handler.handleSelect(this);
    }

    public UUID getUuid() {
        return uuid;
    }
}

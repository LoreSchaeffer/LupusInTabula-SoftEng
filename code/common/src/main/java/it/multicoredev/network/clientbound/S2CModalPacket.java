package it.multicoredev.network.clientbound;

import it.multicoredev.enums.Message;
import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import org.jetbrains.annotations.NotNull;

public class S2CModalPacket implements Packet<IClientPacketListener> {
    private String id;
    private Message title;
    private Message body;
    private boolean large;

    public S2CModalPacket(@NotNull String id, @NotNull Message title, @NotNull Message body, boolean large) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.large = large;
    }

    public S2CModalPacket(@NotNull String id, @NotNull Message title, @NotNull Message body) {
        this(id, title, body, false);
    }

    public S2CModalPacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (id == null || id.trim().isEmpty()) throw new EncoderException("id is null or empty");
        if (title == null) throw new EncoderException("title is null");
        if (body == null) throw new EncoderException("body is null");

        buf.writeString(id);
        buf.writeInt(title.ordinal());
        buf.writeInt(body.ordinal());
        buf.writeBoolean(large);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        id = buf.readString();
        title = Message.values()[buf.readInt()];
        body = Message.values()[buf.readInt()];
        large = buf.readBoolean();

        if (id == null || id.trim().isEmpty()) throw new DecoderException("id is null or empty");
        if (title == null) throw new DecoderException("title is null");
        if (body == null) throw new DecoderException("body is null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleModal(this);
    }

    public String getId() {
        return id;
    }

    public Message getTitle() {
        return title;
    }

    public Message getBody() {
        return body;
    }

    public boolean isLarge() {
        return large;
    }
}

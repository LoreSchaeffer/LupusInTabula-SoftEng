package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.text.BaseText;
import org.jetbrains.annotations.NotNull;

public class S2CModalPacket implements Packet<IClientPacketListener> {
    private String id;
    private BaseText title;
    private BaseText body;
    private boolean large;

    public S2CModalPacket(@NotNull String id, @NotNull BaseText title, @NotNull BaseText body, boolean large) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.large = large;
    }

    public S2CModalPacket(@NotNull String id, @NotNull BaseText title, @NotNull BaseText body) {
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
        buf.writeObject(title);
        buf.writeObject(body);
        buf.writeBoolean(large);
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        id = buf.readString();
        title = buf.readObject(BaseText.class);
        body = buf.readObject(BaseText.class);
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

    public BaseText getTitle() {
        return title;
    }

    public BaseText getBody() {
        return body;
    }

    public boolean isLarge() {
        return large;
    }
}

package it.multicoredev.network.clientbound;

import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.utils.Encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class S2CMessagePacket implements Packet<IClientPacketListener> {
    private String sender;
    private String message;

    public S2CMessagePacket(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public S2CMessagePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (sender == null || sender.isEmpty()) throw new EncoderException("Sender cannot be null or empty");
        if (message == null || message.trim().isEmpty()) throw new EncoderException("Message cannot be null or empty");

        buf.writeString(sender);

        try {
            buf.writeString(Encryption.encrypt(message));
        } catch (GeneralSecurityException | IOException e) {
            throw new EncoderException(e);
        }
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        sender = buf.readString();

        String encrypted = buf.readString();
        if (encrypted == null) throw new DecoderException("Message cannot be null");

        try {
            message = Encryption.decrypt(encrypted);
        } catch (GeneralSecurityException e) {
            throw new DecoderException(e);
        }

        if (sender == null || sender.isEmpty()) throw new DecoderException("Sender cannot be null or empty");
        if (message.trim().isEmpty()) throw new DecoderException("Message cannot be null or empty");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleMessage(this);
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}

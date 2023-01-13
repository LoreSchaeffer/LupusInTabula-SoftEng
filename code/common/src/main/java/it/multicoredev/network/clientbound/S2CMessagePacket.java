package it.multicoredev.network.clientbound;

import it.multicoredev.enums.MessageChannel;
import it.multicoredev.mclib.network.PacketByteBuf;
import it.multicoredev.mclib.network.exceptions.DecoderException;
import it.multicoredev.mclib.network.exceptions.EncoderException;
import it.multicoredev.mclib.network.exceptions.ProcessException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.text.BaseText;
import it.multicoredev.text.StaticText;
import it.multicoredev.text.TranslatableText;
import it.multicoredev.utils.Encryption;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class S2CMessagePacket implements Packet<IClientPacketListener> {
    private BaseText sender;
    private BaseText message;
    private MessageChannel channel;

    public S2CMessagePacket(@NotNull BaseText sender, @NotNull BaseText message, @NotNull MessageChannel channel) {
        this.sender = sender;
        this.message = message;
        this.channel = channel;
    }

    public S2CMessagePacket() {
    }

    @Override
    public void encode(PacketByteBuf buf) throws EncoderException {
        if (sender == null) throw new EncoderException("Sender cannot be null");
        if (sender instanceof StaticText && sender.getText().trim().isEmpty()) throw new EncoderException("Sender cannot be empty");
        if (message == null) throw new EncoderException("Message cannot be null");
        if (message instanceof StaticText && message.getText().trim().isEmpty()) throw new EncoderException("Message cannot be empty");

        buf.writeObject(sender);

        try {
            if (message instanceof StaticText staticMessage) {
                StaticText encryptedMessage = new StaticText(Encryption.encrypt(staticMessage.getText()));
                buf.writeObject(encryptedMessage);
            } else if (message instanceof TranslatableText translatableMessage) {
                Object[] args = translatableMessage.getArgs();
                Object[] encryptedArgs = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof String string) encryptedArgs[i] = Encryption.encrypt(string);
                    else if (args[i] instanceof StaticText staticTextArg) args[i] = new StaticText(Encryption.encrypt(staticTextArg.getText()));
                    else encryptedArgs[i] = args[i];
                }

                buf.writeObject(new TranslatableText(translatableMessage.getPath(), encryptedArgs));
            } else {
                throw new EncoderException("Unknown message type " + message.getClass().getName());
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new EncoderException(e);
        }

        buf.writeInt(channel.ordinal());
    }

    @Override
    public void decode(PacketByteBuf buf) throws DecoderException {
        sender = buf.readObject(BaseText.class);

        BaseText encryptedMessage = buf.readObject(BaseText.class);
        if (encryptedMessage == null) throw new DecoderException("Message cannot be null");

        try {
            if (encryptedMessage instanceof StaticText staticMessage) {
                message = new StaticText(Encryption.decrypt(staticMessage.getText()));
            } else if (encryptedMessage instanceof TranslatableText translatableMessage) {
                Object[] encryptedArgs = translatableMessage.getArgs();
                Object[] args = new Object[encryptedArgs.length];

                for (int i = 0; i < encryptedArgs.length; i++) {
                    if (encryptedArgs[i] instanceof String string) args[i] = Encryption.decrypt(string);
                    else if (encryptedArgs[i] instanceof StaticText staticTextArg) args[i] = new StaticText(Encryption.decrypt(staticTextArg.getText()));
                    else args[i] = encryptedArgs[i];
                }

                message = new TranslatableText(translatableMessage.getPath(), args);
            }
        } catch (GeneralSecurityException e) {
            throw new DecoderException(e);
        }

        channel = MessageChannel.values()[buf.readInt()];

        if (sender == null) throw new DecoderException("Sender cannot be null or empty");
        if (message instanceof StaticText && message.getText().trim().isEmpty()) throw new DecoderException("Message cannot be empty");
        if (channel == null) throw new DecoderException("Channel cannot be null");
    }

    @Override
    public void processPacket(IClientPacketListener handler) throws ProcessException {
        handler.handleMessage(this);
    }

    public BaseText getSender() {
        return sender;
    }

    public BaseText getMessage() {
        return message;
    }

    public MessageChannel getChannel() {
        return channel;
    }
}

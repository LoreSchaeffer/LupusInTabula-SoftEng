package it.multicoredev.client.ui.comms.messages.b2f;

import org.jetbrains.annotations.NotNull;

public class ChatMessageMessage extends B2FMessage {
    private String channel;
    private String from;
    private String message;

    public ChatMessageMessage(@NotNull String channel, @NotNull String from, @NotNull String message) {
        super("chat_message");
        this.channel = channel;
        this.from = from;
        this.message = message;
    }
}

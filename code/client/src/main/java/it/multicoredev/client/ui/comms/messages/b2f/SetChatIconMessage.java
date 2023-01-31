package it.multicoredev.client.ui.comms.messages.b2f;

import org.jetbrains.annotations.NotNull;

public class SetChatIconMessage extends B2FMessage {
    private String icon;

    public SetChatIconMessage(@NotNull String icon) {
        super("set_chat_icon");
        this.icon = icon;
    }
}

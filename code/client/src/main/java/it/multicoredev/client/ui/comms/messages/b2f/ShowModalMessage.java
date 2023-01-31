package it.multicoredev.client.ui.comms.messages.b2f;

import org.jetbrains.annotations.NotNull;

public class ShowModalMessage extends B2FMessage {
    private final String id;
    private final String title;
    private final String body;
    private final Boolean large;
    private final boolean custom;

    public ShowModalMessage(@NotNull String id, @NotNull String title, @NotNull String body, boolean large, boolean custom) {
        super("show_modal");
        this.id = id;
        this.title = title;
        this.body = body;
        this.large = large;
        this.custom = custom;
    }

    public ShowModalMessage(String id, String title, String body, boolean custom) {
        this(id, title, body, false, custom);
    }

    public ShowModalMessage(String id, String title, String body) {
        this(id, title, body, false, false);
    }
}

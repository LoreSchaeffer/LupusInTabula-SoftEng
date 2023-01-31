package it.multicoredev.client.ui.comms.messages.b2f;

public abstract class B2FMessage {
    private final String type;

    public B2FMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

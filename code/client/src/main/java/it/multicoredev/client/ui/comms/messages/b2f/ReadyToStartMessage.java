package it.multicoredev.client.ui.comms.messages.b2f;

public class ReadyToStartMessage extends B2FMessage {
    public boolean ready;

    public ReadyToStartMessage(boolean ready) {
        super("ready_to_start");
        this.ready = ready;
    }
}

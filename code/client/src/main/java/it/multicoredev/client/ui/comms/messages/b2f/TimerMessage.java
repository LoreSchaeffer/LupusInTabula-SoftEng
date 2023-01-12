package it.multicoredev.client.ui.comms.messages.b2f;

public class TimerMessage extends B2FMessage {
    private final int time;

    public TimerMessage(int time) {
        super("timer");
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}

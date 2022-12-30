package it.multicoredev.client.ui.comms.messages.b2f;

public class GameStartCountdownMessage extends B2FMessage {
    private final int value;

    public GameStartCountdownMessage(int value) {
        super("game_start_countdown");
        this.value = value;
    }
}

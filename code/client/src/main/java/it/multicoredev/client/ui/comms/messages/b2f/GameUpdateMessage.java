package it.multicoredev.client.ui.comms.messages.b2f;

import it.multicoredev.models.Game;
import org.jetbrains.annotations.NotNull;

public class GameUpdateMessage extends B2FMessage {
    private final Game game;

    public GameUpdateMessage(@NotNull Game game) {
        super("game_update");
        this.game = game;
    }
}

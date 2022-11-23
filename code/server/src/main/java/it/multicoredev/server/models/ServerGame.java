package it.multicoredev.server.models;

import it.multicoredev.models.Game;
import it.multicoredev.models.GameState;
import org.jetbrains.annotations.NotNull;

public class ServerGame extends Game {

    public ServerGame(@NotNull String code, @NotNull ServerPlayer master) {
        super(code);

        addPlayer(master);
    }

    public void start() {
        state = GameState.STARTING;
    }

    public void loop() {
        state = GameState.RUNNING;
    }

    public void stop() {
        state = GameState.STOPPED;
    }

    public void end() {
        state = GameState.ENDING;
    }
}

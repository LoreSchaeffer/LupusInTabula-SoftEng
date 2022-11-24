package it.multicoredev.server.models;

import it.multicoredev.models.Game;
import it.multicoredev.models.GameState;
import it.multicoredev.models.SceneIds;
import it.multicoredev.network.clientbound.S2CChangeScenePacket;
import it.multicoredev.server.LupusInTabula;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerGame extends Game {
    private final LupusInTabula lit;
    private ScheduledFuture<?> gameTask;

    public ServerGame(@NotNull String code, @NotNull ServerPlayer master, @NotNull LupusInTabula lit) {
        super(code);

        this.lit = lit;
        addPlayer(master);
    }

    public void init() {
        state = GameState.WAITING;
    }

    public void start() {
        state = GameState.STARTING;

        gameTask = LupusInTabula.SCHEDULER.schedule(this::play, 0, TimeUnit.SECONDS);
    }

    public void play() {
        // TODO Countdown

        state = GameState.RUNNING;

        //TODO Game
    }

    public void stop() {
        state = GameState.STOPPED;
    }

    public void end() {
        state = GameState.ENDING;
    }

    @Override
    @Nullable
    public ServerPlayer getPlayer(@NotNull UUID id) {
        return (ServerPlayer) super.getPlayer(id);
    }

    @Override
    public ServerPlayer getMater() {
        return (ServerPlayer) super.getMater();
    }
}

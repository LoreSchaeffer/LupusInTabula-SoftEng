package it.multicoredev.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Game {
    protected final String code;
    protected final List<Player> players = new ArrayList<>();
    protected GameState state;
    protected int day;
    protected boolean night;

    public Game(@NotNull String code) {
        this.code = code;
        this.state = GameState.WAITING;
        this.day = 1;
        this.night = false;
    }

    public String getCode() {
        return code;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Game addPlayer(@NotNull Player player) {
        players.add(player);
        return this;
    }

    @Nullable
    public Player getPlayer(@NotNull UUID id) {
        return players.stream().filter(p -> p.getUniqueId().equals(id)).findFirst().orElse(null);
    }

    public Player getMater() {
        return players.stream().filter(Player::isMaster).findFirst().orElse(null);
    }

    public GameState getState() {
        return state;
    }

    public int getDay() {
        return day;
    }

    public int nextDay() {
        day++;
        return day;
    }

    public boolean isNight() {
        return night;
    }

    public Game setDay() {
        night = false;
        return this;
    }

    public Game setNight() {
        night = true;
        return this;
    }

    protected void setState(GameState state) {
        this.state = state;
    }
}

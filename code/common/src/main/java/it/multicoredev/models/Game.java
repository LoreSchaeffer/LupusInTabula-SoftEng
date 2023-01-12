package it.multicoredev.models;

import it.multicoredev.enums.GameState;
import it.multicoredev.enums.Role;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Game {
    public static final int MIN_PLAYERS = 8;
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

    public Game removePlayer(@NotNull Player player) {
        players.remove(player);
        return this;
    }

    @Nullable
    public Player getPlayer(@NotNull UUID id) {
        return players.stream().filter(p -> p.getUniqueId().equals(id)).findFirst().orElse(null);
    }

    public List<Player> getPlayersByRole(@NotNull Role role) {
        return players.stream().filter(p -> role.equals(p.getRole())).collect(Collectors.toList());
    }

    @Nullable
    public Player getPlayerByRole(@NotNull Role role) {
        return getPlayersByRole(role).get(0);
    }

    public Player getMaster() {
        return players.stream().filter(Player::isMaster).findFirst().orElse(null);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public GameState getState() {
        return state;
    }

    public int getDay() {
        return day;
    }

    public boolean isNight() {
        return night;
    }

    public Game setDay() {
        night = false;
        day++;
        return this;
    }

    public Game setNight() {
        night = true;
        return this;
    }

    protected void setState(@NotNull GameState state) {
        this.state = state;
    }

    protected boolean roleExists(@NotNull Role role) {
        return players.stream().anyMatch(p -> role.equals(p.getRole()));
    }
}

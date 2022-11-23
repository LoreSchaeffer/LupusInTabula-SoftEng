package it.multicoredev.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Player {
    private final UUID uuid;
    private final String name;
    private final boolean gameMaster;
    private Roles role;
    private boolean alive;

    public Player(@NotNull UUID uuid, @NotNull String name, boolean gameMaster) {
        this.uuid = uuid;
        this.name = name;
        this.gameMaster = gameMaster;
        this.alive = true;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isGameMaster() {
        return gameMaster;
    }

    public Roles getRole() {
        return role;
    }

    public Player setRole(@NotNull Roles role) {
        this.role = role;
        return this;
    }

    public boolean isAlive() {
        return alive;
    }

    public Player kill() {
        alive = false;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return uuid.equals(player.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

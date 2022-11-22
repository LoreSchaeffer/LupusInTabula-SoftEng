package it.multicoredev.models;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Player {
    private final UUID uuid;
    private final String name;
    private Roles role;
    private boolean alive;

    public Player(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
        this.alive = true;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
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
}

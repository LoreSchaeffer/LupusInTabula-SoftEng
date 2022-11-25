package it.multicoredev.models;

import it.multicoredev.enums.Role;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Player extends Client {
    private boolean master;
    private Role role;
    private boolean alive;

    public Player(@NotNull UUID uuid, @NotNull String name, boolean master) {
        super(uuid, name);
        this.master = master;
        this.alive = true;
    }

    public Player (@NotNull Client client, boolean master) {
        this(client.getUniqueId(), client.getName(), master);
    }

    public boolean isMaster() {
        return master;
    }

    public Player setMaster(boolean master) {
        this.master = master;
        return this;
    }

    public Role getRole() {
        return role;
    }

    public Player setRole(@NotNull Role role) {
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

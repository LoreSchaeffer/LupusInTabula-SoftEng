package it.multicoredev.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Client {
    protected final UUID uuid;
    protected final String name;

    public Client(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return uuid.equals(client.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return name + " (" + uuid + ")";
    }
}

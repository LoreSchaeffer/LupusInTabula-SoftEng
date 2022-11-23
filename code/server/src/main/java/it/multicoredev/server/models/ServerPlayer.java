package it.multicoredev.server.models;

import it.multicoredev.models.Player;
import it.multicoredev.server.network.ServerNetHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerPlayer extends Player {
    private ServerNetHandler netHandler;

    public ServerPlayer(@NotNull UUID uuid, @NotNull String name, boolean gameMaster, @NotNull ServerNetHandler netHandler) {
        super(uuid, name, gameMaster);

        this.netHandler = netHandler;
    }
}

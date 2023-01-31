package it.multicoredev.client.ui.comms.messages.b2f;

import it.multicoredev.models.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinMessage extends B2FMessage {
    private final Player player;

    public PlayerJoinMessage(@NotNull Player player) {
        super("player_join");
        this.player = player;
    }
}

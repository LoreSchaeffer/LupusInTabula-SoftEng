package it.multicoredev.client.ui.comms.messages.b2f;

import it.multicoredev.models.Player;
import org.jetbrains.annotations.NotNull;

public class UpdatePlayerMessage extends B2FMessage {
    private final Player player;
    private final boolean active;

    public UpdatePlayerMessage(@NotNull Player player, boolean active) {
        super("update_player");
        this.player = player;
        this.active = active;
    }
}

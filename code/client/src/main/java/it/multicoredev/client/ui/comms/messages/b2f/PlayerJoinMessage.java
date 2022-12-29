package it.multicoredev.client.ui.comms.messages.b2f;

import it.multicoredev.models.Player;

public class PlayerJoinMessage extends B2FMessage {
    private final Player player;

    public PlayerJoinMessage(Player player) {
        super("player_join");
        this.player = player;
    }
}

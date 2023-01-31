package it.multicoredev.client.ui.comms.messages.b2f;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerLeaveMessage extends B2FMessage {
    @SerializedName("client_id")
    private final UUID clientId;

    public PlayerLeaveMessage(@NotNull UUID clientId) {
        super("player_leave");
        this.clientId = clientId;
    }
}

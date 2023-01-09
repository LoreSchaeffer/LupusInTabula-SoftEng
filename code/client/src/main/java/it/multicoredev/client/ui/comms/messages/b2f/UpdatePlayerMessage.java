package it.multicoredev.client.ui.comms.messages.b2f;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.models.Player;
import org.jetbrains.annotations.NotNull;

public class UpdatePlayerMessage extends B2FMessage {
    private final Player player;
    @SerializedName("show_role")
    private final boolean showRole;
    private final boolean active;

    public UpdatePlayerMessage(@NotNull Player player, boolean showRole, boolean active) {
        super("update_player");
        this.player = player;
        this.showRole = showRole;
        this.active = active;
    }
}

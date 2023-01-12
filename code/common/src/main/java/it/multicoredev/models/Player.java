package it.multicoredev.models;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.enums.MessageChannel;
import it.multicoredev.enums.Role;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Player extends Client {
    protected boolean master;
    protected Role role;
    protected boolean alive;
    @SerializedName("message_channel")
    protected MessageChannel messageChannel;
    @SerializedName("role_known_by")
    protected Set<UUID> roleKnownBy;

    public Player(@NotNull UUID uuid, @NotNull String name, boolean master) {
        super(uuid, name);
        this.master = master;
        this.alive = true;
        this.messageChannel = MessageChannel.ALL;
        this.roleKnownBy = new HashSet<>();
    }

    public Player(@NotNull Client client, boolean master) {
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

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public Player setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
        return this;
    }

    public boolean knowsRole(@NotNull UUID uuid) {
        return roleKnownBy != null && roleKnownBy.contains(uuid);
    }

    public Player addRoleKnownBy(@NotNull Player player) {
        roleKnownBy.add(player.getUniqueId());
        return this;
    }
}

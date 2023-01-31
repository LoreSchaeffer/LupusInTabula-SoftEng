package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.enums.MessageChannel;
import it.multicoredev.enums.Role;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import it.multicoredev.network.serverbound.C2SSelectPacket;
import it.multicoredev.text.Text;
import it.multicoredev.utils.LitLogger;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerClickMessage extends F2BMessage {
    private final String id;

    public PlayerClickMessage(@NotNull String id) {
        super("player_click");
        this.id = id;
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception ignored) {
            LitLogger.warn("Selected an invalid id: " + id);
            return false;
        }

        Game game = lit.getCurrentGame();
        if (game == null) {
            LitLogger.warn("Selected a player while not in a game");
            return false;
        }

        Player player = lit.getPlayer();
        if (player == null) {
            LitLogger.warn("Selected a player but your player is not present");
            return false;
        }

        Player target = game.getPlayer(uuid);
        if (target == null) {
            LitLogger.warn("Selected a player that is not present in the game");
            return false;
        }

        if (player.equals(target)) {
            lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_YOURSELF.getText(), MessageChannel.SYSTEM);
            return true;
        }

        if (player.getRole().equals(Role.SEER)) {
            if (target.roleIsKnownBy(player.getUniqueId())) {
                lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_ALREADY_KNOWN.getText(), MessageChannel.SYSTEM);
                return true;
            }
        } else if (player.getRole().equals(Role.OWL_MAN)) {
            if (!target.isAlive()) {
                lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_DEAD.getText(), MessageChannel.SYSTEM);
                return true;
            }
        } else if (player.getRole().equals(Role.MYTHOMANIAC)) {
            if (!target.isAlive()) {
                lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_DEAD.getText(), MessageChannel.SYSTEM);
                return true;
            }
        } else if (player.getRole().equals(Role.BODYGUARD)) {
            if (!target.isAlive()) {
                lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_DEAD.getText(), MessageChannel.SYSTEM);
                return true;
            }
        } else if (player.getRole().equals(Role.WEREWOLF)) {
            if (!target.isAlive()) {
                lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_DEAD.getText(), MessageChannel.SYSTEM);
                return true;
            }

            if (target.getRole().equals(Role.WEREWOLF)) {
                lit.sendChatMessage(Text.CHANNEL_SYSTEM.getText(), Text.SELECT_WEREWOLF.getText(), MessageChannel.SYSTEM);
                return true;
            }
        }

        lit.sendPacket(new C2SSelectPacket(uuid));

        return true;
    }
}

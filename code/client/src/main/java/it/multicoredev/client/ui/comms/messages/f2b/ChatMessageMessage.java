package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.models.Player;
import it.multicoredev.network.serverbound.C2SMessagePacket;
import it.multicoredev.utils.LitLogger;
import org.cef.callback.CefQueryCallback;

public class ChatMessageMessage extends F2BMessage {
    private String message;

    public ChatMessageMessage() {
        super("chat_message");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        Player player = LupusInTabula.get().getPlayer();
        if (player == null) return false;

        try {
            LupusInTabula.get().sendPacket(new C2SMessagePacket(message, player.getMessageChannel()));
        } catch (PacketSendException e) {
            LitLogger.get().warn(e.getMessage(), e);
            return false;
        }

        return true;
    }
}

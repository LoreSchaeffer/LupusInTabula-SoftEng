package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;

public class JoinGameMessage extends F2BMessage {
    private final String code;

    public JoinGameMessage(@NotNull String code) {
        super("join_game");
        this.code = code;
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        if (code == null || code.trim().isEmpty()) {
            if (Static.DEBUG) LitLogger.get().error("Join game request has no data");
            return false;
        }

        lit.joinGame(code);
        return true;
    }
}

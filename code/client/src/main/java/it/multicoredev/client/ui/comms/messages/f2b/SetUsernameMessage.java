package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;

public class SetUsernameMessage extends F2BMessage {
    private final String username;

    public SetUsernameMessage(@NotNull String username) {
        super("set_username");
        this.username = username;
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        if (username == null || username.trim().isEmpty()) {
            if (Static.DEBUG) LitLogger.error("Set username request has no data");
            return false;
        }

        lit.setUsername(username);
        return true;
    }
}

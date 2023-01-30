package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;

public class ReplayMessage extends F2BMessage {


    public ReplayMessage() {
        super("replay");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        lit.setScene(Scene.LOBBY);
        return true;
    }
}

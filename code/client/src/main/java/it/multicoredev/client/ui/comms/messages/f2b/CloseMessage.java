package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import org.cef.callback.CefQueryCallback;

public class CloseMessage extends F2BMessage {

    public CloseMessage() {
        super("close");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        lit.stop();
        return true;
    }
}

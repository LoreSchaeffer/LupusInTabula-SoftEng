package it.multicoredev.client.ui.comms.messages;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.comms.InternalMessage;
import org.cef.callback.CefQueryCallback;

public class CloseMessage extends InternalMessage {

    public CloseMessage() {
        super("close");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        lit.stop();
        return true;
    }
}

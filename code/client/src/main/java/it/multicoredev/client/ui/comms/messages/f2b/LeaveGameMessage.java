package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import org.cef.callback.CefQueryCallback;

public class LeaveGameMessage extends F2BMessage {

    public LeaveGameMessage() {
        super("leave_game");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        lit.leaveGame();
        return true;
    }
}

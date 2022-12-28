package it.multicoredev.client.ui.comms.messages;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.comms.InternalMessage;
import org.cef.callback.CefQueryCallback;

public class NewGameMessage extends InternalMessage {

    public NewGameMessage() {
        super("new_game");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        lit.createGame();
        return true;
    }
}

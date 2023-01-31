package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.models.Player;
import org.cef.callback.CefQueryCallback;

import static it.multicoredev.utils.Static.GSON;

public class GetWinnerMessage extends F2BMessage {

    public GetWinnerMessage() {
        super("get_winner");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        callback.success(lit.getWinner().name());
        return true;
    }
}

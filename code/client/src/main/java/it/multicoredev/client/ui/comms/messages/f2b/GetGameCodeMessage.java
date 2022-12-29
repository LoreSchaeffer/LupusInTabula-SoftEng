package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import org.cef.callback.CefQueryCallback;

public class GetGameCodeMessage extends F2BMessage {

    public GetGameCodeMessage() {
        super("get_game_code");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        callback.success(lit.getCurrentGame().getCode());
        return true;
    }
}

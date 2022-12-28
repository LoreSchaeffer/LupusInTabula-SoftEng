package it.multicoredev.client.ui.comms.messages;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.comms.InternalMessage;
import org.cef.callback.CefQueryCallback;

public class GetGameCodeMessage extends InternalMessage {

    public GetGameCodeMessage() {
        super("get_game_code");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        callback.success(lit.getCurrentGame().getCode());
        return true;
    }
}

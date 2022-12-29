package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import org.cef.callback.CefQueryCallback;

import static it.multicoredev.utils.Static.GSON;

public class GetSelfMessage extends F2BMessage {

    public GetSelfMessage() {
        super("get_self");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        Game game = lit.getCurrentGame();
        if (game == null) {
            return false;
        }

        Player player = game.getPlayer(lit.getClientId());
        if (player == null) {
            return false;
        }

        callback.success(GSON.toJson(player));
        return true;
    }
}

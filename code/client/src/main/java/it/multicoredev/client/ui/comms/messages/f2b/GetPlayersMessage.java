package it.multicoredev.client.ui.comms.messages.f2b;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import org.cef.callback.CefQueryCallback;

import static it.multicoredev.utils.Static.GSON;

public class GetPlayersMessage extends F2BMessage {

    public GetPlayersMessage() {
        super("get_players");
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        Game game = lit.getCurrentGame();
        if (game == null) {
            return false;
        }

        callback.success(GSON.toJson(game.getPlayers()));
        return true;
    }
}

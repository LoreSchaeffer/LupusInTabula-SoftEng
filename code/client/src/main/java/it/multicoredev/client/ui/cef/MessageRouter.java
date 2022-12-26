package it.multicoredev.client.ui.cef;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.InternalMessage;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import static it.multicoredev.utils.Static.GSON;

public class MessageRouter extends CefMessageRouterHandlerAdapter {
    private LupusInTabula lit;
    private Gui gui;

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        if (lit == null) lit = LupusInTabula.get();
        if (gui == null) gui = Gui.get();

        InternalMessage msg;

        try {
            msg = GSON.fromJson(request, InternalMessage.class);
            if (msg == null || msg.getType() == null) throw new NullPointerException();
        } catch (Exception ignored) {
            if (Static.DEBUG) LitLogger.get().warn("Invalid message: " + request);
            return false;
        }

        switch (msg.getType()) {
            case "close":
                lit.stop();
            case "set_scene":
                if (!msg.hasData()) {
                    if (Static.DEBUG) LitLogger.get().error("Set scene request has no data");
                    return false;
                }

                String sceneId;
                try {
                    sceneId = (String) msg.getData().get(0);
                } catch (ClassCastException ignored) {
                    if (Static.DEBUG) LitLogger.get().error("Set scene request has invalid data");
                    return false;
                }

                Scene scene = Scene.fromId(sceneId);
                if (scene == null) {
                    if (Static.DEBUG) LitLogger.get().error("Set scene request has invalid scene id");
                    return false;
                }

                gui.setScene(scene);
                return true;
            case "new_game":
                lit.createGame();
                return true;
            case "join_game":
                if (!msg.hasData()) {
                    if (Static.DEBUG) LitLogger.get().error("Join game request has no data");
                    return false;
                }

                String code;
                try {
                    code = (String) msg.getData().get(0);
                } catch (ClassCastException ignored) {
                    if (Static.DEBUG) LitLogger.get().error("Join game request has invalid data");
                    return false;
                }

                lit.joinGame(code);
                return true;
            case "bootstrap":
                callback.success(String.valueOf(lit.bootstrapProgress));
                return true;
            default:
                if (Static.DEBUG) LitLogger.get().warn("Unknown message type: " + msg.getType());
                return false;
        }
    }
}

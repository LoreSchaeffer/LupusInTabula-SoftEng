package it.multicoredev.client.ui.cef;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.InternalMessage;
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
            case "bootstrap":
                callback.success(String.valueOf(lit.bootstrapProgress));
                return true;
            default:
                if (Static.DEBUG) LitLogger.get().warn("Unknown message type: " + msg.getType());
                return false;
        }
    }
}

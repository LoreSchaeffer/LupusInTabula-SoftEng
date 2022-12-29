package it.multicoredev.client.ui.comms;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.comms.messages.f2b.F2BMessage;
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

        F2BMessage msg;

        try {
            msg = GSON.fromJson(request, F2BMessage.class);
            if (msg == null || msg.getType() == null) throw new NullPointerException();
        } catch (Exception ignored) {
            if (Static.DEBUG) LitLogger.get().warn("Unknown message: " + request);
            return false;
        }

        return msg.process(lit, gui, callback);
    }
}

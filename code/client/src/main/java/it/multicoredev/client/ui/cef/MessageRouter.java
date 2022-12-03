package it.multicoredev.client.ui.cef;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

public class MessageRouter extends CefMessageRouterHandlerAdapter {

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
        if (request.indexOf("BindingTest:") == 0) {
            String msg = request.substring(12);
            callback.success(new StringBuilder(msg).reverse().toString());
            return true;
        }

        return false;
    }
}

package it.multicoredev.client.ui.cef;

import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.callback.CefSchemeRegistrar;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

public class AppHandler extends CefAppHandlerAdapter {

    public AppHandler(String[] args) {
        super(args);
    }

    @Override
    public void onRegisterCustomSchemes(CefSchemeRegistrar registrar) {
        registrar.addCustomScheme(LocalSchemeHandler.SCHEME, true, false, false, false, false, false, false);
    }

    @Override
    public void onContextInitialized() {
        CefApp app = CefApp.getInstance();
        app.registerSchemeHandlerFactory(LocalSchemeHandler.SCHEME, "", new SchemeHandlerFactory());
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState state) {
        if (state == CefApp.CefAppState.TERMINATED) System.exit(0);
    }

    private class SchemeHandlerFactory implements CefSchemeHandlerFactory {

        @Override
        public CefResourceHandler create(CefBrowser browser, CefFrame frame, String schemeName, CefRequest request) {
            if (schemeName.equals(LocalSchemeHandler.SCHEME)) return new LocalSchemeHandler();
            return null;
        }
    }
}

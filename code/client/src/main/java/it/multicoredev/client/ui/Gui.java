package it.multicoredev.client.ui;

import it.multicoredev.client.ui.cef.AppHandler;
import it.multicoredev.client.ui.cef.MessageRouter;
import it.multicoredev.client.ui.components.CircularProgressBar;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import me.friwi.jcefmaven.*;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandler;
import org.cef.handler.CefDisplayHandler;
import org.cef.handler.CefKeyboardHandler;
import org.cef.handler.CefLoadHandler;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Gui extends JFrame {
    private final int width;
    private final int height;
    private final CefApp app;
    private final CefClient client;
    private CefBrowser browser;

    private static Gui instance;

    private Gui(int width, int height) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        this.width = width;
        this.height = height;

        CefAppBuilder cab = new CefAppBuilder();
        cab.setInstallDir(new File("cef"));
        cab.setProgressHandler(new ProgressHandler());

        cab.getCefSettings().windowless_rendering_enabled = true;
        cab.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_ERROR;

        cab.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0);
            }
        });

        CefApp.addAppHandler(new AppHandler(new String[0]));
        app = cab.build();

        client = app.createClient();
        client.addContextMenuHandler(new CefContextMenuHandler() {
            @Override
            public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
                model.clear();
            }

            @Override
            public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {
                return false;
            }

            @Override
            public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {

            }
        });
        client.addKeyboardHandler(new CefKeyboardHandler() {
            @Override
            public boolean onPreKeyEvent(CefBrowser browser, CefKeyEvent event, BoolRef is_keyboard_shortcut) {
                return false;
            }

            @Override
            public boolean onKeyEvent(CefBrowser browser, CefKeyEvent event) {
                if (!event.type.equals(CefKeyEvent.EventType.KEYEVENT_KEYUP)) return false;

                if (event.windows_key_code == 116) {
                    if (Static.DEBUG) {
                        //browser.reloadIgnoreCache();
                        loadURL(browser.getURL());
                        return true;
                    }
                }/* else if (event.windows_key_code == 123) {
                    if (Static.DEBUG) {

                    }
                }*/

                return false;
            }
        });

        CefMessageRouter router = CefMessageRouter.create();
        router.addHandler(new MessageRouter(), true);
        client.addMessageRouter(router);
        client.addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (Static.DEBUG) LitLogger.get().info("Browser loading state changed to : " + isLoading);
            }

            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                if (Static.DEBUG) LitLogger.get().info("Browser loading started");
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if (Static.DEBUG) LitLogger.get().info("Browser loading ended");
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                if (Static.DEBUG) LitLogger.get().info("Browser loading error: [" + errorCode + "] " + errorText + " (" + failedUrl + ")");
            }
        });
        client.addDisplayHandler(new CefDisplayHandler() {
            @Override
            public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String s) {

            }

            @Override
            public void onTitleChange(CefBrowser cefBrowser, String s) {

            }

            @Override
            public boolean onTooltip(CefBrowser cefBrowser, String s) {
                return false;
            }

            @Override
            public void onStatusMessage(CefBrowser cefBrowser, String s) {

            }

            @Override
            public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String text, String file, int line) {
                if (Static.DEBUG) {
                    String log = "JS: [" + file + ":" + line + "] " +  text;

                    switch (logSeverity) {
                        case LOGSEVERITY_ERROR, LOGSEVERITY_FATAL -> LitLogger.get().error(log);
                        case LOGSEVERITY_WARNING -> LitLogger.get().warn(log);
                        default -> LitLogger.get().info(log);
                    }

                    return true;
                }

                return false;
            }

            @Override
            public boolean onCursorChange(CefBrowser cefBrowser, int i) {
                return false;
            }
        });
    }

    public static Gui create(int width, int height) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        if (instance != null) throw new IllegalStateException("Gui already created");
        instance = new Gui(width, height);

        return instance;
    }

    public static Gui get() {
        if (instance == null) throw new IllegalStateException("Gui not created");
        return instance;
    }

    public void show(@NotNull String url) {
        if (browser != null) throw new IllegalStateException("Browser already created");

        browser = client.createBrowser(url, true, false);

        Component browserUI = browser.getUIComponent();
        getContentPane().add(browserUI, BorderLayout.CENTER);

        pack();
        setSize(width, height);
        setLocationRelativeTo(null);

        Image image = IconLoader.loadIcon("assets/icon.png");
        if (image != null) setIconImage(image);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public void loadURL(String url) {
        browser.loadURL(url);
    }

    private static class ProgressHandler implements IProgressHandler {
        private JFrame frame;
        private JProgressBar progressBar;

        @Override
        public void handleProgress(EnumProgress progress, float percentage) {
            if (Static.DEBUG) LitLogger.get().info(progress.name() + " browser " + percentage);

            if (!progress.equals(EnumProgress.DOWNLOADING)) return;

            if (frame == null) {
                frame = new JFrame("Downloading natives..."); //TODO Localize message
                frame.setSize(500, 500);
                frame.setLocationRelativeTo(null);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });

                Image image = IconLoader.loadIcon("assets/icon.png");
                if (image != null) frame.setIconImage(image);

                progressBar = new JProgressBar();
                progressBar.setUI(new CircularProgressBar());
                progressBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                progressBar.setStringPainted(true);
                progressBar.setFont(progressBar.getFont().deriveFont(24f));
                progressBar.setBackground(new Color(21, 21, 21));
                progressBar.setForeground(Color.ORANGE);

                frame.getContentPane().add(progressBar);

                frame.setVisible(true);
            }

            progressBar.setValue((int) percentage);

            if (percentage == 100) {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            }
        }
    }
}

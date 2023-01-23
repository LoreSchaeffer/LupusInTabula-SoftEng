package it.multicoredev.client.ui;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.cef.AppHandler;
import it.multicoredev.client.ui.comms.MessageRouter;
import it.multicoredev.client.ui.comms.messages.b2f.B2FMessage;
import it.multicoredev.client.ui.components.CircularProgressBar;
import it.multicoredev.enums.SceneId;
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
    private final boolean startMaximized;
    private Image icon;
    private final CefApp app;
    private final CefClient client;
    private CefBrowser browser;
    private boolean fullscreen = false;
    private JDialog devToolsDialog;
    private boolean ready = false;
    private SceneId currentScene;

    private static Gui instance;

    private Gui(int width, int height, boolean startMaximized) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        this.width = width;
        this.height = height;
        this.startMaximized = startMaximized;

        CefAppBuilder cab = new CefAppBuilder();
        cab.setInstallDir(new File("cef"));
        cab.setProgressHandler(new ProgressHandler());

        cab.getCefSettings().windowless_rendering_enabled = true;
        cab.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_ERROR;
        cab.getCefSettings().log_file = "cef.log";

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

                if (event.windows_key_code == 116) { // F5
                    if (Static.DEBUG) {
                        browser.reloadIgnoreCache();
                        return true;
                    }
                } else if (event.windows_key_code == 122) { // F11
                    dispose();
                    setUndecorated(!isUndecorated());

                    if (!fullscreen) setSize(2560, 1440);
                    else setSize(width, height);

                    setVisible(true);
                    fullscreen = !fullscreen;

                    return true;
                } else if (event.windows_key_code == 123) { // F12
                    if (Static.DEBUG) {
                        if (devToolsDialog == null) {
                            devToolsDialog = new JDialog();
                            devToolsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            devToolsDialog.setSize(800, 600);
                            devToolsDialog.add(browser.getDevTools().getUIComponent());
                            if (icon != null) devToolsDialog.setIconImage(icon);
                            devToolsDialog.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent e) {
                                    devToolsDialog = null;
                                }
                            });
                            devToolsDialog.setVisible(true);
                        } else {
                            devToolsDialog.dispose();
                            devToolsDialog = null;
                        }
                    }
                }

                return false;
            }
        });

        CefMessageRouter router = CefMessageRouter.create();
        router.addHandler(new MessageRouter(), true);
        client.addMessageRouter(router);
        client.addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (Static.DEBUG) LitLogger.info("Browser loading state changed to : " + isLoading);
                ready = !isLoading;
            }

            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                if (Static.DEBUG) LitLogger.info("Browser loading started");
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if (Static.DEBUG) LitLogger.info("Browser loading ended");
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                if (Static.DEBUG) LitLogger.info("Browser loading error: [" + errorCode + "] " + errorText + " (" + failedUrl + ")");
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
                    String log = "JS: [" + file + ":" + line + "] " + text;

                    switch (logSeverity) {
                        case LOGSEVERITY_ERROR, LOGSEVERITY_FATAL -> LitLogger.error(log);
                        case LOGSEVERITY_WARNING -> LitLogger.warn(log);
                        default -> LitLogger.info(log);
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

    public static Gui create(int width, int height, boolean startMaximized) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        if (instance != null) throw new IllegalStateException("Gui already created");
        instance = new Gui(width, height, startMaximized);

        return instance;
    }

    public static Gui get() {
        if (instance == null) throw new IllegalStateException("Gui not created");
        return instance;
    }

    public void show(@NotNull Scene scene) {
        if (browser != null) throw new IllegalStateException("Browser already created");

        browser = client.createBrowser(scene.getUrl(), true, false);

        Component browserUI = browser.getUIComponent();
        getContentPane().add(browserUI, BorderLayout.CENTER);

        pack();
        setSize(width, height);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);
        setTitle("Lupus In Tabula");
        if (startMaximized) setExtendedState(MAXIMIZED_BOTH);

        icon = IconLoader.loadIcon("assets/icon.png");
        if (icon != null) setIconImage(icon);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LupusInTabula.get().stop();
            }
        });

        setVisible(true);
    }

    public boolean isReady() {
        return ready;
    }

    public void loadURL(@NotNull String url) {
        ready = false;
        browser.loadURL(url);
    }

    public void setScene(@NotNull Scene scene) {
        ready = false;
        currentScene = scene.getId();
        loadURL(scene.getUrl());
    }

    public void setScene(@NotNull SceneId id) {
        Scene scene = Scene.fromId(id);
        if (scene == null) throw new IllegalArgumentException("Invalid scene id: " + id);
        setScene(scene);
    }

    public SceneId getCurrentScene() {
        return currentScene;
    }

    public void executeFrontendCode(@NotNull B2FMessage msg) {
        if (browser == null) throw new IllegalStateException("Browser not created");

        String message = Static.GSON.toJson(msg);
        browser.executeJavaScript("onMessage(" + message + ")", browser.getURL(), 0);

        if (Static.DEBUG) LitLogger.info("Executing frontend code: " + message);
    }

    public void close() {
        if (browser != null) browser.close(true);
        if (client != null) client.dispose();
        if (app != null) app.dispose();
        CefApp.getInstance().dispose();
    }

    private static class ProgressHandler implements IProgressHandler {
        private JFrame frame;
        private JProgressBar progressBar;

        @Override
        public void handleProgress(EnumProgress progress, float percentage) {
            if (Static.DEBUG) LitLogger.info(progress.name() + " browser " + percentage);

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

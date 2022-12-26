package it.multicoredev.client;

import it.multicoredev.client.assets.Config;
import it.multicoredev.client.assets.Locale;
import it.multicoredev.client.network.ClientNetSocket;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Utils;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LupusInTabula {
    private static final GsonHelper GSON = new GsonHelper();
    private static LupusInTabula instance;

    private final File confDir = new File("conf");
    private final File localizationsDir = new File(confDir, "localizations");
    private Config config;
    private Map<String, Locale> localizations = new HashMap<>();

    private Gui gui;
    private final ClientNetSocket netSocket;
    // Placeholder vars
    public int bootstrapProgress = 0;

    //TODO Automatic update

    private LupusInTabula() {
        netSocket = new ClientNetSocket();
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        initConfigs();
        extractAssets();

        try {
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

            int[] dimensions;
            if (screenHeight <= 1080) dimensions = new int[]{1280, 720};
            else dimensions = new int[]{1920, 1080};

            gui = Gui.create(dimensions[0], dimensions[1], false);
            gui.show(Scene.BOOTSTRAP);
        } catch (Exception e) {
            LitLogger.get().error("Failed to start GUI", e);
        }

        gui.setVisible(true);

        //TODO Test code
        Utils.sleep(1000);

        for (int i = 0; i < 101; i++) {
            gui.executeFrontendCode("{\"type\":\"bootstrap\",\"data\": " + i + "}");
            bootstrapProgress++;
            Utils.sleep(5);
        }
        //TODO End of test code

        gui.setScene(Scene.MAIN_MENU);
    }

    public void stop() {
        if (netSocket.isConnected()) netSocket.disconnect();
        gui.close();
        System.exit(0);
    }

    private void initConfigs() {
        if (!confDir.exists() || !confDir.isDirectory()) {
            if (!confDir.mkdir()) {
                LitLogger.get().error("Could not create conf directory!");
                System.exit(-1);
            }
        }

        try {
            config = GSON.autoload(new File("conf", "config.json"), new Config().init(), Config.class);
        } catch (Exception e) {
            LitLogger.get().error("Cannot create/load config file!", e);
            System.exit(-1);
        }

        if (!localizationsDir.exists() || !localizationsDir.isDirectory()) {
            if (!localizationsDir.mkdir()) {
                LitLogger.get().error("Could not create localizations directory!");
                System.exit(-1);
            }
        }

        File[] files = localizationsDir.listFiles();
        if (files == null || files.length == 0) {
            Locale en = new Locale().init();

            try {
                GSON.save(en, new File(localizationsDir, "en_us.json"));
            } catch (Exception e) {
                LitLogger.get().error("Cannot create default localization file!", e);
                System.exit(-1);
            }

            localizations.put("en_us", en);
        } else {
            for (File file : files) {
                if (file.isDirectory() || !file.getName().toLowerCase().endsWith(".json")) return;

                try {
                    Locale locale = GSON.load(file, Locale.class);
                    if (locale == null) continue;

                    try {
                        if (locale.completeMissing()) {
                            GSON.save(locale, file);
                        }
                    } catch (Exception e) {
                        LitLogger.get().error("Cannot save localization file " + file.getName() + "!", e);
                    }

                    localizations.put(file.getName().toLowerCase().replace(".json", ""), locale);
                } catch (Exception e) {
                    LitLogger.get().error("Cannot load localization file " + file.getName() + "!", e);
                }
            }

            if (localizations.isEmpty() || !localizations.containsKey("en_us")) {
                Locale en = new Locale().init();

                try {
                    GSON.save(en, new File(localizationsDir, "en_us.json"));
                } catch (Exception e) {
                    LitLogger.get().error("Cannot create default localization file!", e);
                    System.exit(-1);
                }
            }
        }
    }

    private void extractAssets() {
        //TODO Assets can be extracted
    }

    private void checkForUpdates() {
        //TODO Check for updates
        // Placeholder code
        for (int i = 0; i < 100; i++) {
            Utils.sleep(100);
            bootstrapProgress = i;
        }
    }
}

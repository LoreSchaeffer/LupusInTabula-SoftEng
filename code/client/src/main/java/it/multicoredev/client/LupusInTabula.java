package it.multicoredev.client;

import it.multicoredev.client.assets.Config;
import it.multicoredev.client.assets.Locale;
import it.multicoredev.client.network.ClientNetSocket;
import it.multicoredev.client.ui.Window;
import it.multicoredev.client.utils.ServerAddress;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SMessagePacket;
import it.multicoredev.utils.LitLogger;

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

    private final Window window;
    private final ClientNetSocket netSocket;

    public LupusInTabula() {
        window = Window.create(1280, 720, "Lupus in Tabula");
        netSocket = new ClientNetSocket();
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        initConfigs();
        window.run();



        //TODO Debug code here
        netSocket.connect(ServerAddress.fromString(config.serverAddress));

        LitLogger.get().info("Waiting for connection...");
        while (!netSocket.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LitLogger.get().info("Connected!");

        netSocket.sendPacket(new C2SHandshakePacket(netSocket.getClientId(), "Test"));
        netSocket.sendPacket(new C2SMessagePacket("Hello world!"));
        //TODO End of debug code
    }

    public void stop() {
        if (netSocket != null) {
            netSocket.disconnect();
        }
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
}

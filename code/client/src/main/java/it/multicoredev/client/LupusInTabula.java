package it.multicoredev.client;

import it.multicoredev.client.assets.Config;
import it.multicoredev.client.assets.Locale;
import it.multicoredev.client.network.ClientNetSocket;
import it.multicoredev.client.utils.ServerAddress;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.network.serverbound.C2SCreateGame;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SJoinGamePacket;
import it.multicoredev.network.serverbound.C2SStartGamePacket;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Utils;

import java.io.File;
import java.util.*;

public class LupusInTabula {
    private static final GsonHelper GSON = new GsonHelper();
    private static LupusInTabula instance;

    private final File confDir = new File("conf");
    private final File localizationsDir = new File(confDir, "localizations");
    private Config config;
    private Map<String, Locale> localizations = new HashMap<>();

    private final ClientNetSocket netSocket;

    public LupusInTabula() {
        netSocket = new ClientNetSocket();
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        initConfigs();


        //TODO Debug code here
        Scanner scanner = new Scanner(System.in);

        String in;
        while ((in = scanner.nextLine()) != null) {
            String command;
            String[] args;

            if (!in.contains(" ")) {
                command = in;
                args = new String[0];
            } else {
                String[] tmp = in.split(" ");
                command = tmp[0];
                args = Arrays.copyOfRange(tmp, 1, tmp.length);
            }

            switch (command) {
                case "connect" -> {
                    if (args.length > 0) netSocket.connect(ServerAddress.fromString(args[0]));
                    else netSocket.connect(ServerAddress.fromString(config.serverAddress));

                    while (!netSocket.isConnected()) {
                        Utils.sleep(10);
                    }

                    netSocket.sendPacket(new C2SHandshakePacket(UUID.randomUUID(), "Player-" + new Random().nextInt(100)));
                }
                case "disconnect" -> netSocket.disconnect();
                case "create" -> netSocket.sendPacket(new C2SCreateGame());
                case "join" -> {
                    if (args.length == 0) {
                        LitLogger.get().error("Missing game code");
                        break;
                    }

                    netSocket.sendPacket(new C2SJoinGamePacket(args[0]));
                }
                case "start" -> {
                    if (args.length == 0) {
                        LitLogger.get().error("Missing game code");
                        break;
                    }

                    netSocket.sendPacket(new C2SStartGamePacket());
                }
            }
        }
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

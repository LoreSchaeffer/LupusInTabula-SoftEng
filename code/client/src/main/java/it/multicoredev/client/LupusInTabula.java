package it.multicoredev.client;

import it.multicoredev.client.assets.Config;
import it.multicoredev.client.assets.Locale;
import it.multicoredev.client.network.ClientNetSocket;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.client.ui.comms.messages.b2f.B2FMessage;
import it.multicoredev.client.ui.comms.messages.b2f.ChatMessageMessage;
import it.multicoredev.client.ui.comms.messages.b2f.ShowModalMessage;
import it.multicoredev.client.utils.ServerAddress;
import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.enums.MessageChannel;
import it.multicoredev.enums.SceneId;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import it.multicoredev.network.serverbound.C2SCreateGame;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SJoinGamePacket;
import it.multicoredev.network.serverbound.C2SStartGamePacket;
import it.multicoredev.text.BaseText;
import it.multicoredev.text.StaticText;
import it.multicoredev.text.Text;
import it.multicoredev.text.TranslatableText;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LupusInTabula {
    private static final GsonHelper GSON = new GsonHelper();
    private static LupusInTabula instance;

    private final File confDir = new File("conf");
    private final File localizationsDir = new File(confDir, "localizations");
    private Config config;
    private final Map<String, Locale> localizations = new HashMap<>();

    private Gui gui;
    private final ClientNetSocket net;
    private Game currentGame;

    // Placeholder vars
    public int bootstrapProgress = 0;

    //TODO Automatic update

    private LupusInTabula() {
        net = new ClientNetSocket();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
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
            LitLogger.error("Failed to start GUI", e);
        }

        gui.setVisible(true);

        //TODO Test code
//        Utils.sleep(1000);

//        for (int i = 0; i < 101; i++) {
//            gui.executeFrontendCode("{\"type\":\"bootstrap\",\"data\": " + i + "}");
//            bootstrapProgress++;
//            Utils.sleep(100);
//        }
        //TODO End of test code

        setScene(Scene.MAIN_MENU);
        if (config.username == null || config.username.trim().isEmpty()) {
            while (!gui.isReady()) {
                Utils.sleep(10);
            }
            showModal(
                    "username_selection",
                    "Choose your name",
                    "<input id=\"nameSelector\" class=\"form-control form-control-lg\" type=\"text\">",
                    true
            ); //TODO Localize
        }
    }

    public void stop() {
        System.exit(0);
    }

    public void setScene(Scene scene) {
        gui.setScene(scene);
    }

    public void setScene(SceneId scene) {
        gui.setScene(scene);
    }

    public SceneId getCurrentScene() {
        return gui.getCurrentScene();
    }

    public void executeFrontendCode(@NotNull B2FMessage msg) {
        gui.executeFrontendCode(msg);
    }

    public void showModal(String id, String title, String body, boolean large, boolean custom) {
        gui.executeFrontendCode(new ShowModalMessage(id, title, body, large, custom));
    }

    public void showModal(String id, String title, String body, boolean custom) {
        showModal(id, title, body, false, custom);
    }

    public void showModal(String id, String title, String body) {
        showModal(id, title, body, false, false);
    }

    public void createGame() {
        if (!connectToServer()) {
            LitLogger.info("Connection to server " + config.serverAddress + " failed");
            showModal("connection_error", "Connection error", "Failed to connect to server"); //TODO Localize
            return;
        }
        LitLogger.info("Connected to server " + config.serverAddress);

        net.sendPacket(new C2SCreateGame());
    }

    public void joinGame(String code) {
        if (!connectToServer()) {
            LitLogger.info("Connection to server " + config.serverAddress + " failed");
            showModal("connection_error", "Connection error", "Failed to connect to server"); //TODO Localize
            return;
        }
        LitLogger.info("Connected to server " + config.serverAddress);

        net.sendPacket(new C2SJoinGamePacket(code));
    }

    public void leaveGame() {
        if (currentGame == null) return;

        try {
            net.disconnect(DisconnectReason.C2S_QUIT_GAME);
        } catch (PacketSendException e) {
            LitLogger.error("Failed to send quit game packet", e);
        }

        currentGame = null;
        setScene(Scene.MAIN_MENU);
    }

    public void startGame() {
        try {
            net.sendPacket(new C2SStartGamePacket());
        } catch (PacketSendException e) {
            LitLogger.warn("Failed to send start game packet", e);
        }
    }

    public void setCurrentGame(Game game) {
        currentGame = game;
    }

    @Nullable
    public Game getCurrentGame() {
        return currentGame;
    }

    @Nullable
    public Player getPlayer() {
        Game game = getCurrentGame();
        if (game == null) return null;

        return game.getPlayer(getClientId());
    }

    public UUID getClientId() {
        return net.getClientId();
    }

    public void setUsername(@NotNull String username) {
        config.username = username;
        saveConfig();
    }

    public void sendChatMessage(BaseText sender, BaseText message, MessageChannel channel) {
        Locale locale = getLocale();

        String senderText = sender instanceof StaticText ? sender.getText() : ((TranslatableText) sender).setLocalization(locale).getText();
        String messageText = message instanceof StaticText ? message.getText() : ((TranslatableText) message).setLocalization(locale).getText();


        executeFrontendCode(new ChatMessageMessage(
                new TranslatableText(Text.byPath("channel." + channel.getId()).getPath()).setLocalization(locale).getText(),
                senderText,
                messageText
        ));

        LitLogger.info("CHAT: " + channel.name() + " - " + senderText + " > " + messageText);
    }

    public void sendPacket(Packet<?> packet) throws PacketSendException {
        net.sendPacket(packet);
    }

    public Locale getLocale() {
        return localizations.getOrDefault(config.language, localizations.get("en_us"));
    }

    private void initConfigs() {
        if (!confDir.exists() || !confDir.isDirectory()) {
            if (!confDir.mkdir()) {
                LitLogger.error("Could not create conf directory!");
                System.exit(-1);
            }
        }

        try {
            config = GSON.autoload(new File("conf", "config.json"), new Config().init(), Config.class);
        } catch (Exception e) {
            LitLogger.error("Cannot create/load config file!", e);
            System.exit(-1);
        }

        if (!localizationsDir.exists() || !localizationsDir.isDirectory()) {
            if (!localizationsDir.mkdir()) {
                LitLogger.error("Could not create localizations directory!");
                System.exit(-1);
            }
        }

        File[] files = localizationsDir.listFiles();
        if (files == null || files.length == 0) {
            Locale en = new Locale().init();

            try {
                GSON.save(en, new File(localizationsDir, "en_us.json"));
            } catch (Exception e) {
                LitLogger.error("Cannot create default localization file!", e);
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
                        LitLogger.error("Cannot save localization file " + file.getName() + "!", e);
                    }

                    localizations.put(file.getName().toLowerCase().replace(".json", ""), locale);
                } catch (Exception e) {
                    LitLogger.error("Cannot load localization file " + file.getName() + "!", e);
                }
            }

            if (localizations.isEmpty() || !localizations.containsKey("en_us")) {
                Locale en = new Locale().init();

                try {
                    GSON.save(en, new File(localizationsDir, "en_us.json"));
                } catch (Exception e) {
                    LitLogger.error("Cannot create default localization file!", e);
                    System.exit(-1);
                }
            }
        }
    }

    private void saveConfig() {
        new Thread(() -> {
            try {
                GSON.save(config, new File("conf", "config.json"));
            } catch (Exception e) {
                LitLogger.error("Cannot save config file!", e);
            }
        }).start();
    }

    private void extractAssets() {
        //TODO Assets can be extracted
    }

    private void checkForUpdates() {
        //TODO Check for updates
        // Placeholder code

    }

    private boolean connectToServer() {
        net.connect(ServerAddress.fromString(config.serverAddress));

        // Maximum wait time 3 seconds
        for (int i = 0; i < 60; i++) {
            if (net.isConnected()) {
                net.sendPacket(new C2SHandshakePacket(net.getClientId(), config.username));

                for (int j = i; j < 60; j++) {
                    if (net.isHandshakeDone()) {
                        return true;
                    }

                    Utils.sleep(50);
                }
            }
            Utils.sleep(50);
        }

        return false;
    }

    private void shutdown() {
        if (net.isConnected()) net.disconnect();
        if (gui != null) gui.close();
    }
}

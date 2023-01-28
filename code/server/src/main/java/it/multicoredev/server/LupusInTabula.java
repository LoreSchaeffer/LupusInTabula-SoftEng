package it.multicoredev.server;

import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import it.multicoredev.server.assets.Config;
import it.multicoredev.server.models.ServerGame;
import it.multicoredev.server.models.ServerPlayer;
import it.multicoredev.server.network.ServerNetSocket;
import it.multicoredev.server.utils.ServerUtils;
import it.multicoredev.utils.Encryption;
import it.multicoredev.utils.LitLogger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LupusInTabula {
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 4);

    private static final GsonHelper GSON = new GsonHelper();
    private static final int CODE_LENGTH = 6;
    private static final int MAX_GAMES = Integer.MAX_VALUE;

    private static LupusInTabula instance;

    private final File confDir = new File("conf");
    private Config config;

    private ServerNetSocket net;
    private final Map<String, ServerGame> games = new HashMap<>();

    private LupusInTabula() {

    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        initConfigs();

        Encryption.setSecret(UUID.randomUUID().toString());

        net = new ServerNetSocket(config.port);
        net.start();
    }

    public ServerNetSocket netSocket() {
        return net;
    }

    public Config config() {
        return config;
    }

    @Nullable
    public ServerGame getGame(String code) {
        return games.get(code);
    }

    @Nullable
    public ServerGame getGame(Player player) {
        return games.values().stream().filter(g -> g.getPlayers().contains(player)).findFirst().orElse(null);
    }

    @Nullable
    public ServerGame getGame(Client client) {
        return games.values().stream().filter(g -> g.getPlayer(client.getUniqueId()) != null).findFirst().orElse(null);
    }

    public void addGame(ServerGame game) {
        games.put(game.getCode(), game);
    }

    public void removeGame(String code) {
        games.remove(code);
    }

    public Game createGame(ServerPlayer master) {
        ServerGame game = new ServerGame(createCode(), master, this);
        addGame(game);

        game.init();

        return game;
    }

    public UUID getNewClientId() {
        return net.getNewClientId();
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
    }

    private String createCode() {
        String code;

        do {
            code = ServerUtils.createRandomCode(CODE_LENGTH);
        } while (getGame(code) != null);

        return code;
    }
}

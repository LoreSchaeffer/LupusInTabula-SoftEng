package it.multicoredev.server;

import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import it.multicoredev.server.models.ServerGame;
import it.multicoredev.server.models.ServerPlayer;
import it.multicoredev.server.network.ServerNetSocket;
import it.multicoredev.server.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LupusInTabula {
    private static LupusInTabula instance;
    private static final int CODE_LENGTH = 6;

    private final ServerNetSocket netSocket;
    private final Map<String, ServerGame> games = new HashMap<>();

    private LupusInTabula() {
        netSocket = new ServerNetSocket(12987);
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        netSocket.start();
    }

    public ServerNetSocket getNetSocket() {
        return netSocket;
    }

    @Nullable
    public ServerGame getGame(String code) {
        return games.get(code);
    }

    @Nullable
    public ServerGame getGame(Player player) {
        return games.values().stream().filter(g -> g.getPlayers().contains(player)).findFirst().orElse(null);
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

    private String createCode() {
        String code;

        do {
            code = Utils.createRandomCode(CODE_LENGTH);
        } while (getGame(code) != null);

        return code;
    }
}

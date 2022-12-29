package it.multicoredev.server.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.enums.GameState;
import it.multicoredev.enums.Role;
import it.multicoredev.enums.SceneId;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@JsonAdapter(ServerGame.Adapter.class)
public class ServerGame extends Game {
    private final LupusInTabula lit;
    private ScheduledFuture<?> gameTask;

    public ServerGame(@NotNull String code, @NotNull ServerPlayer master, @NotNull LupusInTabula lit) {
        super(code);

        this.lit = lit;
        addPlayer(master);
    }

    public void init() {
        state = GameState.WAITING;
        LitLogger.get().info("Game " + code + " initialized. Waiting for players...");
    }

    public void start() {
        state = GameState.STARTING;
        LitLogger.get().info("Game " + code + " starting in seconds...");
        gameTask = LupusInTabula.SCHEDULER.schedule(this::play, 0, TimeUnit.SECONDS);
    }

    public void play() {
        broadcast(new S2CChangeScenePacket(SceneId.STARTING));

        for (int i = lit.config().gameStartingCountdown; i >= 0; i--) {
            broadcast(new S2CGameStartCountdownPacket(i));
            Utils.sleep(1000);
        }

        state = GameState.RUNNING;
        LitLogger.get().info("Game " + code + " started!");

        assignRoles();
        broadcast(new S2CGamePacket(this));
        broadcast(new S2CChangeScenePacket(SceneId.GAME));

        wait(5);

        //TODO Game
    }

    public void stop() {
        gameTask.cancel(true);
        state = GameState.STOPPED;
        lit.removeGame(this.code);
    }

    public void end() {
        state = GameState.ENDING;
    }

    @Override
    @Nullable
    public ServerPlayer getPlayer(@NotNull UUID id) {
        return (ServerPlayer) super.getPlayer(id);
    }

    @Override
    public ServerPlayer getMater() {
        return (ServerPlayer) super.getMater();
    }

    public List<ServerPlayer> getOnlinePlayers() {
        return players.stream().map(p -> (ServerPlayer) p).filter(ServerPlayer::isConnected).collect(Collectors.toList());
    }

    public void playerDisconnected(Client client) {
        //TODO Handle player disconnection

        Player player = getPlayer(client.getUniqueId());
        if (player == null) return;

        if (player.isMaster()) player.setMaster(false);
        for (ServerPlayer p : getOnlinePlayers()) {
            if (p.equals(player)) continue;
            p.setMaster(true);
            return;
        }

        // Differentiate behaviour based on game state


        if (getOnlinePlayers().isEmpty()) stop();

        broadcast(new S2CPlayerLeavePacket(client.getUniqueId(), players.size() >= MIN_PLAYERS));
    }

    public void broadcast(Packet<?> packet) {
        players.forEach(p -> ((ServerPlayer) p).sendPacket(packet));
    }

    private void assignRoles() {
        List<ServerPlayer> mixedPlayers = new ArrayList<>(players.stream().map(p -> (ServerPlayer) p).toList());
        Collections.shuffle(mixedPlayers);

        List<Role> roles = new ArrayList<>();

        int playerCount = getPlayerCount();
        if (playerCount == 8) {
            roles.add(Role.WEREWOLF);
            roles.add(Role.WEREWOLF);
            roles.add(Role.SEER);

            for (int i = 0; i < playerCount - roles.size(); i++) {
                roles.add(Role.VILLAGER);
            }
        } else if (playerCount < 16) {
            roles.add(Role.WEREWOLF);
            roles.add(Role.WEREWOLF);
            roles.add(Role.SEER);
            roles.add(Role.MEDIUM);
            roles.add(Role.BODYGUARD);
            roles.add(Role.FREEMASON);
            roles.add(Role.FREEMASON);
            roles.add(Role.OWN_MAN);

            for (int i = 0; i < playerCount - roles.size(); i++) {
                roles.add(Role.VILLAGER);
            }
        } else {
            roles.add(Role.WEREWOLF);
            roles.add(Role.WEREWOLF);
            roles.add(Role.WEREWOLF);
            roles.add(Role.SEER);
            roles.add(Role.MEDIUM);
            roles.add(Role.BODYGUARD);
            roles.add(Role.POSSESSED);
            roles.add(Role.FREEMASON);
            roles.add(Role.FREEMASON);
            roles.add(Role.OWN_MAN);
            roles.add(Role.WEREHAMSTER);
            roles.add(Role.MYTHOMANIAC);

            for (int i = 0; i < playerCount - roles.size(); i++) {
                roles.add(Role.VILLAGER);
            }
        }

        for (int i = 0; i < playerCount; i++) {
            mixedPlayers.get(i).setRole(roles.get(i));
        }
    }

    private void wait(int seconds) {
        for (int timer = seconds; timer >= 0; timer--) {
            S2CTimerPacket packet = new S2CTimerPacket(timer);
            broadcast(packet);
            Utils.sleep(1000);
        }
    }

    public static class Adapter implements JsonSerializer<ServerGame> {

        @Override
        public JsonElement serialize(ServerGame game, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(game, Game.class);
        }
    }
}

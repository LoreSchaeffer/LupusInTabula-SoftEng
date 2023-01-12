package it.multicoredev.server.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.enums.*;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.models.Player;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import it.multicoredev.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static it.multicoredev.enums.Role.*;
import static it.multicoredev.utils.Utils.sleep;

@JsonAdapter(ServerGame.Adapter.class)
public class ServerGame extends Game {
    private static final long LONG_SLEEP = 2000;
    private static final long MEDIUM_SLEEP = 1000;
    private static final long SHORT_SLEEP = 500;

    private final LupusInTabula lit;
    private ScheduledFuture<?> gameTask;
    private Thread waitThread;
    private ServerPlayer latestLynchedPlayer = null;
    private ServerPlayer owledPlayer = null;
    private ServerPlayer protectedPlayer = null;
    private final List<ServerPlayer> targets = new ArrayList<>();

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

        if (Static.DEBUG) {
            LitLogger.get().info("Adding fake players...");

            for (int i = players.size() + 1; i <= MIN_PLAYERS; i++) {
                ServerPlayer p = new ServerPlayer(lit.getNewClientId(), "Player " + i, false, null);
                addPlayer(p);
                LitLogger.get().info("Added player " + Static.GSON.toJson(p));
            }

        }

        gameTask = LupusInTabula.SCHEDULER.schedule(() -> {
            try {
                play();
            } catch (Exception e) {
                stop();
                LitLogger.get().error(e.getMessage(), e);
            }
        }, 0, TimeUnit.SECONDS);
    }

    public void play() {
        broadcast(new S2CChangeScenePacket(SceneId.STARTING));
        sleep(200);

        for (int i = lit.config().gameStartingCountdown; i > 0; i--) {
            broadcast(new S2CGameStartCountdownPacket(i));
            sleep(1000);
        }

        state = GameState.RUNNING;
        LitLogger.get().info("Game " + code + " started!");

        assignRoles();

        players.forEach(p1 -> players.forEach(p2 -> {
            if (p1.equals(p2)) return;

            if (p1.getRole().equals(WEREWOLF) && p2.getRole().equals(WEREWOLF)) {
                p1.addRoleKnownBy(p2);
                p2.addRoleKnownBy(p1);
                return;
            }

            if (p1.getRole().equals(FREEMASON) && p2.getRole().equals(FREEMASON)) {
                p1.addRoleKnownBy(p2);
                p2.addRoleKnownBy(p1);
            }
        }));

        broadcast(new S2CGamePacket(this));
        broadcast(new S2CChangeScenePacket(SceneId.GAME));

        wait(3);

        while (gameConditionsAreMet()) {
            if (!isNight()) {
                setNight();
                broadcast(new S2CGamePacket(this));

                if (roleExists(MEDIUM) && day >= 2) {
                    ServerPlayer medium = getPlayerByRole(MEDIUM);
                    if (medium == null) throw new RuntimeException("Medium should not be null");

                    medium.sendPacket(S2CTurnPacket.START);
                    sendSysMessage(Message.GAME_TURN_MEDIUM);
                    sleep(MEDIUM_SLEEP);

                    if (medium.isAlive()) {
                        sendSysMessage(latestLynchedPlayer.getRole().equals(WEREWOLF) ? Message.GAME_TURN_MEDIUM_WEREWOLF : Message.GAME_TURN_MEDIUM_VILLAGER, medium);
                    }

                    medium.sendPacket(S2CTurnPacket.END);
                    sleep(LONG_SLEEP);
                }

                if (roleExists(SEER)) {
                    List<ServerPlayer> seers = getServerPlayersByRole(SEER);
                    if (seers.isEmpty()) throw new RuntimeException("There should be at least one seer");

                    sendSysMessage(Message.GAME_TURN_SEER);

                    boolean seersArePlaying = false;
                    for (ServerPlayer seer : seers) {
                        if (seer.isAlive() && seer.isConnected()) {
                            seersArePlaying = true;
                            sendTurnStartMessage(seer);
                        }
                    }

                    if (seersArePlaying) {
                        threadWait(seers.size() == 1 ? lit.config().gameTurnDuration : lit.config().gameTurnDurationGroup);

                        if (targets.isEmpty()) targets.add(getRandomAlivePlayer(seers));

                        ServerPlayer target = pickTarget();
                        if (target == null) throw new RuntimeException("Target should not be null");

                        sendSysMessage(target.getRole().equals(WEREWOLF) ? Message.GAME_TURN_SEER_WEREWOLF : Message.GAME_TURN_SEER_VILLAGER, target);
                        seers.forEach(seer -> seer.sendPacket(S2CTurnPacket.END));
                    } else {
                        threadWait(Utils.randInt(2, lit.config().gameTurnDuration));
                    }

                    sleep(LONG_SLEEP);
                }

                if (roleExists(OWL_MAN)) {
                    owledPlayer = null;

                    ServerPlayer owlman = getPlayerByRole(OWL_MAN);
                    if (owlman == null) throw new RuntimeException("Owl-man should not be null");

                    sendSysMessage(Message.GAME_TURN_OWL_MAN);

                    if (owlman.isAlive() && owlman.isConnected()) {
                        sendTurnStartMessage(owlman);
                        threadWait(lit.config().gameTurnDuration);
                        owlman.sendPacket(S2CTurnPacket.END);

                        if (targets.isEmpty()) targets.add(getRandomAlivePlayer(owlman));

                        owledPlayer = targets.get(0);
                    } else {
                        threadWait(Utils.randInt(2, lit.config().gameTurnDuration));
                    }

                    sleep(LONG_SLEEP);
                }

                if (roleExists(MYTHOMANIAC) && day == 2) {

                }

                if (roleExists(BODYGUARD)) {

                }

                if (roleExists(WEREWOLF)) {

                }
            } else {
                setDay();
                broadcast(new S2CGamePacket(this));


            }
        }
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
    public ServerPlayer getMaster() {
        return (ServerPlayer) super.getMaster();
    }

    public List<ServerPlayer> getOnlinePlayers() {
        return players.stream().map(p -> (ServerPlayer) p).filter(ServerPlayer::isConnected).collect(Collectors.toList());
    }

    public List<ServerPlayer> getServerPlayersByRole(@NotNull Role role) {
        return players.stream().map(p -> (ServerPlayer) p).filter(p -> role.equals(p.getRole())).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ServerPlayer getPlayerByRole(@NotNull Role role) {
        return getServerPlayersByRole(role).get(0);
    }

    public ServerPlayer getRandomPlayer(List<ServerPlayer> blacklist) {
        ServerPlayer randomPlayer;

        do {
            randomPlayer = (ServerPlayer) players.get(new Random().nextInt(players.size()));
        } while (blacklist.contains(randomPlayer));

        return randomPlayer;
    }

    public ServerPlayer getRandomPlayer(ServerPlayer... blacklist) {
        return getRandomPlayer(Arrays.asList(blacklist));
    }

    public ServerPlayer getRandomAlivePlayer(List<ServerPlayer> blacklist) {
        ServerPlayer randomPlayer;

        do {
            randomPlayer = (ServerPlayer) players.get(new Random().nextInt(players.size()));
        } while (!randomPlayer.isAlive() || blacklist.contains(randomPlayer));

        return randomPlayer;
    }

    public ServerPlayer getRandomAlivePlayer(ServerPlayer... blacklist) {
        return getRandomAlivePlayer(Arrays.asList(blacklist));
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

        broadcast(new S2CPlayerLeavePacket(client.getUniqueId(), Static.DEBUG || players.size() >= MIN_PLAYERS));
    }

    public void broadcast(Packet<?> packet) {
        getOnlinePlayers().forEach(p -> p.sendPacket(packet));
    }

    public void sendSysMessage(Message message) {
        broadcast(new S2CMessagePacket(Message.GAME_NARRATOR.getPath(), message.getPath(), MessageChannel.SYSTEM));
    }

    public void sendSysMessage(Message message, ServerPlayer player) {
        if (!player.isConnected()) return;
        player.sendPacket(new S2CMessagePacket(Message.GAME_NARRATOR.getPath(), message.getPath(), MessageChannel.SYSTEM));
    }

    public void sendSysMessage(Message message, List<ServerPlayer> players) {
        players.forEach(p -> sendSysMessage(message, p));
    }

    public void sendTurnStartMessage(ServerPlayer player) {
        targets.clear();
        if (!player.isConnected()) return;
        player.sendPacket(S2CTurnPacket.START);
    }

    private void assignRoles() {
        List<ServerPlayer> mixedPlayers = new ArrayList<>(players.stream().map(p -> (ServerPlayer) p).toList());
        Collections.shuffle(mixedPlayers);

        List<Role> roles = new ArrayList<>();

        int playerCount = getPlayerCount();
        if (playerCount == 8) {
            roles.add(WEREWOLF);
            roles.add(WEREWOLF);
            roles.add(SEER);

            while (roles.size() < playerCount) roles.add(VILLAGER);
        } else if (playerCount < 16) {
            roles.add(WEREWOLF);
            roles.add(WEREWOLF);
            roles.add(SEER);
            roles.add(MEDIUM);
            roles.add(BODYGUARD);
            roles.add(FREEMASON);
            roles.add(FREEMASON);
            roles.add(OWL_MAN);

            while (roles.size() < playerCount) roles.add(VILLAGER);
        } else {
            roles.add(WEREWOLF);
            roles.add(WEREWOLF);
            roles.add(WEREWOLF);
            roles.add(SEER);
            roles.add(MEDIUM);
            roles.add(BODYGUARD);
            roles.add(POSSESSED);
            roles.add(FREEMASON);
            roles.add(FREEMASON);
            roles.add(OWL_MAN);
            roles.add(WEREHAMSTER);
            roles.add(MYTHOMANIAC);

            while (roles.size() < playerCount) roles.add(VILLAGER);
        }

        for (int i = 0; i < playerCount; i++) {
            mixedPlayers.get(i).setRole(roles.get(i));
        }
    }

    private void wait(int seconds) {
        for (int timer = seconds; timer >= -1; timer--) {
            S2CTimerPacket packet = new S2CTimerPacket(timer);
            broadcast(packet);
            sleep(1000);
        }
    }

    private void threadWait(int seconds) {
        waitThread = new Thread(() -> wait(seconds));
        waitThread.start();

        try {
            this.wait(seconds * 1000L);
        } catch (InterruptedException | IllegalMonitorStateException e) {
            LitLogger.get().error(e.getMessage(), e);
        }

        if (waitThread != null) {
            waitThread.interrupt();
            waitThread = null;
        }

        broadcast(new S2CTimerPacket(-1));
    }

    private ServerPlayer pickTarget() { //TODO To test
        return targets.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();
    }

    private boolean gameConditionsAreMet() {
        return true; //TODO
    }

    public static class Adapter implements JsonSerializer<ServerGame> {

        @Override
        public JsonElement serialize(ServerGame game, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(game, Game.class);
        }
    }
}

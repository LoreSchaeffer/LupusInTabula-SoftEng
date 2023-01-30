package it.multicoredev.server.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import it.multicoredev.enums.GameState;
import it.multicoredev.enums.MessageChannel;
import it.multicoredev.enums.Role;
import it.multicoredev.enums.SceneId;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.server.utils.ServerUtils;
import it.multicoredev.text.Text;
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
    private static final long LONG_SLEEP = 3000;
    private static final long MEDIUM_SLEEP = 2000;
    private static final long SHORT_SLEEP = 1000;
    private static final int MIN_TURN_TIME = 5;

    private final LupusInTabula lit;
    private ScheduledFuture<?> gameTask;
    private int timer;
    private Thread waitThread;
    private ServerPlayer lastLynchedPlayer = null;
    private ServerPlayer owledPlayer = null;
    private ServerPlayer protectedPlayer = null;
    private ServerPlayer devouredPlayer = null;
    private final List<ServerPlayer> deadPlayers = new ArrayList<>();
    private final List<ServerPlayer> votedPlayers = new ArrayList<>();
    private final List<ServerPlayer> targets = new ArrayList<>();
    private Role winner = null;

    public ServerGame(@NotNull String code, @NotNull ServerPlayer master, @NotNull LupusInTabula lit) {
        super(code);

        this.lit = lit;
        addPlayer(master);
    }

    public void init() {
        state = GameState.WAITING;
        LitLogger.info("Game " + code + " initialized. Waiting for players...");
    }

    public void start() {
        state = GameState.STARTING;
        LitLogger.info("Game " + code + " starting in seconds...");

        if (Static.DEBUG) {
            LitLogger.info("Adding fake players...");

            for (int i = players.size() + 1; i <= MIN_PLAYERS; i++) {
                ServerPlayer p = new ServerPlayer(lit.getNewClientId(), "Player " + i, false, null);
                addPlayer(p);
                LitLogger.info("Added player " + Static.GSON.toJson(p));
            }

        }

        gameTask = LupusInTabula.SCHEDULER.schedule(() -> {
            try {
                play();
            } catch (Exception e) {
                stop();
                LitLogger.error(e.getMessage(), e);
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
        LitLogger.info("Game " + code + " started!");

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

        broadcast(new S2CGamePacket(this, true));
        broadcast(new S2CChangeScenePacket(SceneId.GAME));

        sleep(MEDIUM_SLEEP);

        while (gameConditionsAreMet()) {
            if (!isNight()) {
                setNight();
                if (Static.DEBUG) LitLogger.info("Starting night " + day);
                broadcast(new S2CGamePacket(this, true));
                sendSysMessage(Text.TIME_NIGHT_START);

                resetData();

                if (roleExists(MEDIUM) && day >= 2) medium();
                if (roleExists(SEER)) seer();
                if (roleExists(OWL_MAN)) owlman();
                if (roleExists(MYTHOMANIAC) && day == 2) mythomaniac();
                if (roleExists(BODYGUARD)) bodyguard();
                if (roleExists(WEREWOLF)) werewolf();
            } else {
                setDay();
                if (Static.DEBUG) LitLogger.info("Starting day " + day);
                broadcast(new S2CGamePacket(this, true));

                checkDeaths();
                if (deadPlayers.isEmpty()) {
                    sendSysMessage(Text.TIME_DAY_START);
                    if (Static.DEBUG) LitLogger.info("During this night no one died");
                } else {
                    sendSysMessage(Text.TIME_DAY_START_DEAD, deadPlayers.stream().map(ServerPlayer::getName).collect(Collectors.joining(", ")));
                    if (Static.DEBUG) LitLogger.info("During this night the following players died: " + deadPlayers.stream().map(Client::toString).collect(Collectors.joining(", ")));
                }

                if (gameConditionsAreMet()) {
                    firstDiscussion();

                    if (!firstVote()) {
                        sendSysMessage(Text.GAME_TURN_LYNCHING_SKIPPED);
                        if (Static.DEBUG) LitLogger.info("No one voted, skipping the lynching");
                        continue;
                    }

                    secondDiscussion();
                    secondVote();
                } else {
                    end();
                }
            }
        }

        end();
    }

    public void stop() {
        LitLogger.info("Game " + code + " stopped");

        if (!gameTask.isCancelled()) gameTask.cancel(true);
        state = GameState.STOPPED;
        lit.removeGame(this.code);

        broadcast(new S2CChangeScenePacket(SceneId.MAIN_MENU));
        broadcast(new S2CModalPacket("game_stop", Text.MODAL_TITLE_GAME_STOPPED.getText(), Text.MODAL_BODY_GAME_STOPPED.getText()));
        getOnlinePlayers().forEach(ServerPlayer::disconnect);
    }

    public void end() {
        LitLogger.info("Game " + code + " ending...");
        state = GameState.ENDING;

        broadcast(new S2CEndGamePacket(winner));
        broadcast(new S2CChangeScenePacket(SceneId.ENDING));

        sleep(SHORT_SLEEP);
        if (!gameTask.isCancelled()) gameTask.cancel(true);
        reset();
        init();
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
        ServerPlayer player = getPlayer(client.getUniqueId());
        if (player == null) return;

        if (player.isMaster()) player.setMaster(false);
        for (ServerPlayer p : getOnlinePlayers()) {
            if (p.equals(player)) continue;
            p.setMaster(true);
            return;
        }

        if (state == GameState.WAITING) {
            broadcast(new S2CPlayerLeavePacket(client.getUniqueId(), Static.DEBUG || players.size() >= MIN_PLAYERS));
        } else if (state == GameState.STARTING) {
            player.setDisconnected();
        } else if (state == GameState.RUNNING) {
            player.setDisconnected();

            if (!gameConditionsAreMet()) {
                end();
                return;
            }

            //TODO Notify other players of the disconnection ?
        }

        broadcast(new S2CGamePacket(this, true));
    }

    public void selectTarget(ServerPlayer player) {
        targets.add(player);
    }

    public void broadcast(Packet<?> packet) {
        getOnlinePlayers().forEach(p -> p.sendPacket(packet));
    }

    public void broadcastMessage(S2CMessagePacket packet, ServerPlayer sender) {
        MessageChannel channel;

        if (sender.getRole() == null) {
            channel = MessageChannel.ALL;
        } else {
            if (!isNight()) {
                channel = MessageChannel.ALL;
            } else {
                if (sender.isAlive()) {
                    if (sender.getRole().equals(WEREWOLF)) channel = MessageChannel.WEREWOLVES;
                    else if (sender.getRole().equals(SEER)) channel = MessageChannel.SEERS;
                    else channel = MessageChannel.ALL;
                } else {
                    channel = MessageChannel.DEAD;
                }
            }
        }

        if (channel.equals(MessageChannel.ALL)) {
            broadcast(packet);
        } else if (channel.equals(MessageChannel.WEREWOLVES)) {
            getServerPlayersByRole(WEREWOLF)
                    .stream()
                    .filter(ServerPlayer::isConnected)
                    .forEach(p -> p.sendPacket(packet));
        } else if (channel.equals(MessageChannel.SEERS)) {
            getServerPlayersByRole(SEER)
                    .stream()
                    .filter(p -> p.isConnected() && p.isAlive())
                    .forEach(p -> p.sendPacket(packet));
        } else {
            players.stream()
                    .map(p -> (ServerPlayer) p)
                    .filter(p -> p.isConnected() && !p.isAlive())
                    .forEach(p -> p.sendPacket(packet));
        }
    }

    public void sendSysMessage(Text msg, Object... args) {
        broadcast(new S2CMessagePacket(Text.NARRATOR.getText(), msg.getText(args), MessageChannel.SYSTEM));
    }

    public void sendSysMessage(ServerPlayer player, Text msg, Object... args) {
        if (!player.isConnected()) return;
        player.sendPacket(new S2CMessagePacket(Text.NARRATOR.getText(), msg.getText(args), MessageChannel.SYSTEM));
    }

    public void sendSysMessage(List<ServerPlayer> players, Text msg, Object... args) {
        players.forEach(p -> sendSysMessage(p, msg, args));
    }

    public void startTurn(ServerPlayer player) {
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
        for (timer = seconds; timer >= -1; timer--) {
            S2CTimerPacket packet = new S2CTimerPacket(timer);
            broadcast(packet);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void threadWait(int seconds, int fakeSeconds) {
        try {
            synchronized (this) {
                if (Static.DEBUG) LitLogger.info("Waiting for " + seconds + "s started");

                waitThread = new Thread(() -> wait(fakeSeconds));
                waitThread.start();
                this.wait(seconds * 1000L);

                if (Static.DEBUG) LitLogger.info("End of wait");
            }
        } catch (InterruptedException | IllegalMonitorStateException e) {
            LitLogger.error(e.getMessage(), e);
        }

        if (waitThread != null) {
            waitThread.interrupt();
            waitThread = null;
        }
    }

    private void multipleThreadWait(int seconds, int players) {
        timer = seconds;

        while (targets.size() < players && timer > 0) {
            seconds = seconds - (seconds - timer);
            threadWait(seconds, seconds);
        }
    }

    private void threadWait(int seconds, int fakeSeconds, int players) {
        if (players > 1) multipleThreadWait(seconds, players);
        else threadWait(seconds, fakeSeconds);

        timer = 0;
        broadcast(new S2CTimerPacket(-1));
    }

    private ServerPlayer pickTarget() {
        return targets.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();
    }

    private boolean gameConditionsAreMet() {
        if (!Static.DEBUG && getOnlinePlayers().isEmpty()) return false;

        List<ServerPlayer> alivePlayers = !Static.DEBUG ?
                getOnlinePlayers().stream().filter(ServerPlayer::isAlive).toList() :
                getPlayers().stream().map(p -> (ServerPlayer) p).filter(ServerPlayer::isAlive).toList();

        int werewolves = (int) alivePlayers.stream().filter(p -> p.getRole().equals(WEREWOLF)).count();
        int villagers = (int) alivePlayers.stream().filter(p -> p.getRole().equals(VILLAGER)).count();

        if (werewolves == 0) {
            sendSysMessage(Text.GAME_WIN_VILLAGERS);
            winner = VILLAGER;
            return false;
        }

        if (werewolves >= villagers) {
            sendSysMessage(Text.GAME_WIN_WEREWOLVES);
            winner = WEREWOLF;
            return false;
        }

        return true;
    }

    private void resetData() {
        owledPlayer = null;
        protectedPlayer = null;
        devouredPlayer = null;
        deadPlayers.clear();
        votedPlayers.clear();
    }

    public void reset() {
        gameTask = null;
        timer = -1;
        waitThread = null;
        lastLynchedPlayer = null;
        owledPlayer = null;
        protectedPlayer = null;
        devouredPlayer = null;
        deadPlayers.clear();
        votedPlayers.clear();
        targets.clear();
        winner = null;
    }

    private void medium() {
        if (Static.DEBUG) LitLogger.info("Starting of Medium's turn");

        ServerPlayer medium = getPlayerByRole(MEDIUM);
        if (medium == null) throw new RuntimeException("Medium should not be null");

        medium.sendPacket(S2CTurnPacket.START);
        sendSysMessage(Text.GAME_TURN_MEDIUM);
        sleep(MEDIUM_SLEEP);

        if (medium.isAlive()) {
            sendSysMessage(medium, lastLynchedPlayer.getRole().equals(WEREWOLF) ? Text.GAME_TURN_MEDIUM_WEREWOLF : Text.GAME_TURN_MEDIUM_VILLAGER, lastLynchedPlayer.getName());
        }

        if (Static.DEBUG) LitLogger.info(lastLynchedPlayer + " " + (lastLynchedPlayer.getRole().equals(WEREWOLF) ? "was" : "wasn't") + " a werewolf");

        lastLynchedPlayer.addRoleKnownBy(medium);
        medium.sendPacket(new S2CGamePacket(this));
        medium.sendPacket(S2CTurnPacket.END);

        sleep(LONG_SLEEP);
        if (Static.DEBUG) LitLogger.info("End of Medium's turn");
    }

    private void seer() {
        if (Static.DEBUG) LitLogger.info("Starting of Seers' turn");

        List<ServerPlayer> seers = getServerPlayersByRole(SEER);
        if (seers.isEmpty()) throw new RuntimeException("There should be at least one seer");

        sendSysMessage(Text.GAME_TURN_SEER);

        boolean seersArePlaying = false;
        for (ServerPlayer seer : seers) {
            if (seer.isAlive() && seer.isConnected()) {
                seersArePlaying = true;
                startTurn(seer);
            }
        }

        if (seersArePlaying) {
            threadWait(lit.config().gameTurnDuration, seers.size());

            if (targets.isEmpty()) targets.add(getRandomAlivePlayer(seers));

            ServerPlayer target = pickTarget();
            if (target == null) throw new RuntimeException("Target should not be null");

            sendSysMessage(seers, target.getRole().equals(WEREWOLF) ? Text.GAME_TURN_SEER_WEREWOLF : Text.GAME_TURN_SEER_VILLAGER, target.getName());

            if (Static.DEBUG) LitLogger.info(target + " " + (target.getRole().equals(WEREWOLF) ? "is" : "isn't") + " a werewolf");

            seers.forEach(seer -> {
                target.addRoleKnownBy(seer);
                seer.sendPacket(new S2CGamePacket(this));
                seer.sendPacket(S2CTurnPacket.END);
            });
        } else {
            threadWait(Utils.randInt(MIN_TURN_TIME, lit.config().gameTurnDuration), lit.config().gameTurnDuration, 1);
        }

        sleep(MEDIUM_SLEEP);

        if (Static.DEBUG) LitLogger.info("End of Seers' turn");
    }

    private void owlman() {
        if (Static.DEBUG) LitLogger.info("Starting of Owlman's turn");
        owledPlayer = null;

        ServerPlayer owlman = getPlayerByRole(OWL_MAN);
        if (owlman == null) throw new RuntimeException("Owl-man should not be null");

        sendSysMessage(Text.GAME_TURN_OWL_MAN);

        if (owlman.isAlive() && owlman.isConnected()) {
            startTurn(owlman);
            threadWait(lit.config().gameTurnDuration, 1);
            owlman.sendPacket(S2CTurnPacket.END);

            if (targets.isEmpty()) targets.add(getRandomAlivePlayer(owlman));
            owledPlayer = targets.get(0);

            if (Static.DEBUG) LitLogger.info("Owled player: " + owledPlayer);
        } else {
            threadWait(Utils.randInt(MIN_TURN_TIME, lit.config().gameTurnDuration), lit.config().gameTurnDuration, 1);
        }

        sleep(MEDIUM_SLEEP);
        if (Static.DEBUG) LitLogger.info("End of Owlman's turn");
    }

    private void mythomaniac() {
        if (Static.DEBUG) LitLogger.info("Starting of Mythomaniac's turn");

        ServerPlayer mythomaniac = getPlayerByRole(MYTHOMANIAC);
        if (mythomaniac == null) throw new RuntimeException("Mythomaniac should not be null");

        sendSysMessage(Text.GAME_TURN_MYTHOMANIAC);

        if (mythomaniac.isAlive() && mythomaniac.isConnected()) {
            startTurn(mythomaniac);
            threadWait(lit.config().gameTurnDuration, 1);

            if (targets.isEmpty()) targets.add(getRandomAlivePlayer(mythomaniac));

            ServerPlayer target = targets.get(0);
            if (target.getRole().equals(WEREWOLF)) mythomaniac.setRole(WEREWOLF);
            else if (target.getRole().equals(SEER)) mythomaniac.setRole(SEER);
            else mythomaniac.setRole(VILLAGER);

            sendSysMessage(mythomaniac, Text.GAME_TURN_MYTHOMANIAC_ROLE, Text.byPath("role." + target.getRole().getId()));
            mythomaniac.sendPacket(new S2CGamePacket(this));
            mythomaniac.sendPacket(S2CTurnPacket.END);

            if (Static.DEBUG) LitLogger.info(mythomaniac + " (mythomaniac) new role is: " + mythomaniac.getRole());
        } else {
            threadWait(Utils.randInt(MIN_TURN_TIME, lit.config().gameTurnDuration), lit.config().gameTurnDuration, 1);
        }

        sleep(MEDIUM_SLEEP);

        if (Static.DEBUG) LitLogger.info("End of Mythomaniac's turn");
    }

    private void bodyguard() {
        if (Static.DEBUG) LitLogger.info("Starting of Bodyguard's turn");

        protectedPlayer = null;

        ServerPlayer bodyguard = getPlayerByRole(BODYGUARD);
        if (bodyguard == null) throw new RuntimeException("Bodyguard should not be null");

        sendSysMessage(Text.GAME_TURN_BODYGUARD);

        if (bodyguard.isAlive() && bodyguard.isConnected()) {
            startTurn(bodyguard);
            threadWait(lit.config().gameTurnDuration, 1);
            bodyguard.sendPacket(S2CTurnPacket.END);

            if (targets.isEmpty()) targets.add(getRandomAlivePlayer(bodyguard));
            protectedPlayer = targets.get(0);

            if (Static.DEBUG) LitLogger.info("Protected player: " + protectedPlayer);
        } else {
            threadWait(Utils.randInt(MIN_TURN_TIME, lit.config().gameTurnDuration), lit.config().gameTurnDuration, 1);
        }

        sleep(MEDIUM_SLEEP);

        if (Static.DEBUG) LitLogger.info("End of Bodyguard's turn");
    }

    private void werewolf() {
        if (Static.DEBUG) LitLogger.info("Starting of Werewolves' turn");

        devouredPlayer = null;

        List<ServerPlayer> werewolves = getServerPlayersByRole(WEREWOLF);
        if (werewolves.isEmpty()) throw new RuntimeException("There should be at least one werewolf");

        sendSysMessage(Text.GAME_TURN_WEREWOLF);

        boolean werewolvesArePlaying = false;
        for (ServerPlayer werewolf : werewolves) {
            if (werewolf.isAlive() && werewolf.isConnected()) {
                werewolvesArePlaying = true;
                startTurn(werewolf);
            }
        }

        if (werewolvesArePlaying) {
            threadWait(lit.config().gameTurnDuration, werewolves.size());

            if (targets.isEmpty()) targets.add(getRandomAlivePlayer(werewolves));


            devouredPlayer = pickTarget();
            if (devouredPlayer == null) throw new RuntimeException("Target should not be null");

            sendSysMessage(werewolves, Text.GAME_TURN_WEREWOLF_CHOICE, devouredPlayer.getName());
            werewolves.forEach(werewolf -> {
                werewolf.sendPacket(new S2CGamePacket(this));
                werewolf.sendPacket(S2CTurnPacket.END);
            });

            if (Static.DEBUG) LitLogger.info("Devoured player: " + devouredPlayer);
        } else {
            threadWait(Utils.randInt(MIN_TURN_TIME, lit.config().gameTurnDuration), lit.config().gameTurnDuration, 1);
        }

        sleep(MEDIUM_SLEEP);

        if (Static.DEBUG) LitLogger.info("End of Werewolves' turn");
    }

    private void checkDeaths() {
        if (devouredPlayer == null) return;
        if (devouredPlayer.equals(protectedPlayer)) return;

        deadPlayers.add(devouredPlayer);
        devouredPlayer.kill();

        broadcast(new S2CGamePacket(this, true));
    }

    private void firstDiscussion() {
        if (Static.DEBUG) LitLogger.info("Starting of first discussion");

        sendSysMessage(Text.GAME_TURN_DISCUSSION_1);
        wait(lit.config().firstDiscussionDuration);
        sendSysMessage(Text.GAME_TURN_DISCUSSION_1_END);

        if (Static.DEBUG) LitLogger.info("End of first discussion");
    }

    private boolean firstVote() {
        if (Static.DEBUG) LitLogger.info("Starting of first vote");

        List<ServerPlayer> votingPlayers = new ArrayList<>(getOnlinePlayers().stream().filter(ServerPlayer::isAlive).toList());
        votingPlayers.add(devouredPlayer);

        sendSysMessage(Text.GAME_TURN_VOTE_1);

        targets.clear();
        votingPlayers.forEach(player -> player.sendPacket(S2CTurnPacket.START));

        threadWait(lit.config().firstVoteDuration, lit.config().firstVoteDuration, votingPlayers.size());

        votingPlayers.forEach(player -> player.sendPacket(S2CTurnPacket.END));

        if (targets.isEmpty()) {
            if (Static.DEBUG) LitLogger.info("End of first vote");
            return false;
        }

        Map<ServerPlayer, Integer> votes = ServerUtils.counter(targets);
        ServerPlayer votedPlayer = ServerUtils.getMostFrequent(votes);
        if (votedPlayer == null) throw new RuntimeException("Voted player should not be null");

        votedPlayers.add(votedPlayer);
        votes.remove(votedPlayer);
        if (Static.DEBUG) LitLogger.info("Voted player 1: " + votedPlayer);

        votedPlayer = ServerUtils.getMostFrequent(votes);
        if (votedPlayer == null) throw new RuntimeException("Voted player should not be null");

        votedPlayers.add(votedPlayer);
        votes.remove(votedPlayer);
        if (Static.DEBUG) LitLogger.info("Voted player 2: " + votedPlayer);

        if (owledPlayer != null && !votedPlayers.contains(owledPlayer)) {
            votedPlayers.add(owledPlayer);
            if (Static.DEBUG) LitLogger.info("Voted player 3 (owled): " + owledPlayer);
        }

        sendSysMessage(Text.GAME_TURN_VOTE_1_END, votedPlayers.stream().map(ServerPlayer::getName).collect(Collectors.joining(", ")));

        if (Static.DEBUG) LitLogger.info("End of first vote");
        return true;
    }

    private void secondDiscussion() {
        if (Static.DEBUG) LitLogger.info("Starting of second discussion");

        sendSysMessage(Text.GAME_TURN_DISCUSSION_2);
        wait(votedPlayers.size() == 3 ? lit.config().secondDiscussionDuration3 : lit.config().secondDiscussionDuration);
        sendSysMessage(Text.GAME_TURN_DISCUSSION_2_END);

        if (Static.DEBUG) LitLogger.info("End of second discussion");
    }

    private void secondVote() {
        if (Static.DEBUG) LitLogger.info("Starting of second vote");

        List<ServerPlayer> votingPlayers = new ArrayList<>(getOnlinePlayers().stream().filter(ServerPlayer::isAlive).toList());

        sendSysMessage(Text.GAME_TURN_VOTE_2, votedPlayers.stream().map(ServerPlayer::getName).collect(Collectors.joining(", ")));

        targets.clear();
        votingPlayers.forEach(player -> player.sendPacket(S2CTurnPacket.START));

        threadWait(lit.config().firstVoteDuration, lit.config().firstVoteDuration, votingPlayers.size());

        votingPlayers.forEach(player -> player.sendPacket(S2CTurnPacket.END));

        if (targets.isEmpty()) {
            if (Static.DEBUG) LitLogger.info("End of second vote");
            return;
        }

        lastLynchedPlayer = pickTarget();
        lastLynchedPlayer.kill();

        broadcast(new S2CGamePacket(this, true));
        sendSysMessage(Text.GAME_TURN_VOTE_2_END, lastLynchedPlayer.getName());
        if (Static.DEBUG) LitLogger.info(lastLynchedPlayer + " was lynched");

        if (Static.DEBUG) LitLogger.info("End of second vote");
    }

    public static class Adapter implements JsonSerializer<ServerGame> {

        @Override
        public JsonElement serialize(ServerGame game, Type type, JsonSerializationContext ctx) {
            return ctx.serialize(game, Game.class);
        }
    }
}

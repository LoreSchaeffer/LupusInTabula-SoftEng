package it.multicoredev.server.network;

import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.enums.SceneId;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.network.IServerPacketListener;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.network.serverbound.*;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.server.models.ServerGame;
import it.multicoredev.server.models.ServerPlayer;
import it.multicoredev.text.StaticText;
import it.multicoredev.text.Text;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static it.multicoredev.utils.Static.DEBUG;

public class ServerPacketListener implements IServerPacketListener {
    private LupusInTabula lit;
    private ServerNetHandler netHandler;
    private Client client;

    @Override
    public void setNetworkHandler(NetworkHandler handler) {
        if (netHandler != null) throw new IllegalStateException("Network handler already set");
        netHandler = (ServerNetHandler) handler;

        lit = LupusInTabula.get();
    }

    @Override
    public void handleHandshake(C2SHandshakePacket packet) {
        //TODO Should have: Check if the client is allowed to connect

        ServerNetSocket netSocket = lit.netSocket();

        UUID newClientId = null;
        if (netSocket.clientExists(packet.getClientId())) newClientId = netSocket.getNewClientId();

        Client client = new Client(newClientId != null ? newClientId : packet.getClientId(), packet.getUsername());
        netSocket.addClient(client, netHandler);
        this.client = client;

        try {
            netHandler.sendPacket(new S2CHandshakePacket(true, null, newClientId));
        } catch (PacketSendException e) {
            LitLogger.error(e.getMessage(), e);
            //TODO Manage exception
        }

        if (DEBUG)
            LitLogger.info("Client '" + packet.getUsername() + "' (" + (newClientId != null ? newClientId : packet.getClientId()) + ") connected");
        //TODO Change this
    }

    @Override
    public void handleMessage(C2SMessagePacket packet) {
        if (client == null) {
            if (DEBUG) LitLogger.error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerGame game = lit.getGame(client);
        if (game == null) {
            if (DEBUG) LitLogger.warn(client + " tried to send a message while not in a game");
            return;
        }

        ServerPlayer sender = game.getPlayer(client.getUniqueId());
        if (sender == null) {
            if (DEBUG) LitLogger.warn(client + " tried to send a message while not in a game");
            return;
        }

        String[] split;
        if (packet.getMessage().contains(" ")) split = packet.getMessage().split(" ");
        else split = new String[]{packet.getMessage()};

        for (int i = 0; i < split.length; i++) {
            String word = split[i];

            if (lit.config().censoredWords.contains(word.toLowerCase())) {
                split[i] = "*".repeat(word.length());
            }
        }

        String censoredMessage = String.join(" ", split);

        game.broadcastMessage(new S2CMessagePacket(new StaticText(sender.getName()), new StaticText(censoredMessage), packet.getChannel()), sender);

        LitLogger.info("CHAT: " + packet.getChannel().name() + " - " + sender + " > " + packet.getMessage());
    }

    @Override
    public void handleCreateGame(C2SCreateGame packet) {
        if (client == null) {
            if (DEBUG) LitLogger.error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerPlayer player = new ServerPlayer(client, true, netHandler);
        Game game = lit.createGame(player);

        try {
            netHandler.sendPacket(new S2CGameCreatedPacket(game));
            netHandler.sendPacket(new S2CChangeScenePacket(SceneId.LOBBY));
        } catch (PacketSendException e) {
            if (DEBUG) LitLogger.error(e.getMessage(), e);
            return;
        }

        if (DEBUG) LitLogger.info(client + " created game with code '" + game.getCode() + "'");
    }

    @Override
    public void handleJoinGame(C2SJoinGamePacket packet) {
        if (client == null) {
            if (DEBUG) LitLogger.error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerGame game = lit.getGame(packet.getCode());

        if (game == null) {
            if (DEBUG)
                LitLogger.info(client + " tried to join a non-existent game with code '" + packet.getCode() + "'");

            try {
                disconnect(DisconnectReason.S2C_GAME_NOT_FOUND);
            } catch (PacketSendException e) {
                if (DEBUG) LitLogger.error(e.getMessage(), e);
            }

            return;
        }

        ServerPlayer player = new ServerPlayer(client, false, netHandler);
        game.addPlayer(player);

        try {
            netHandler.sendPacket(new S2CGameJoinedPacket(game));
            netHandler.sendPacket(new S2CChangeScenePacket(SceneId.LOBBY));
        } catch (PacketSendException e) {
            if (DEBUG) LitLogger.error(e.getMessage(), e);
            return;
        }

        try {
            game.broadcast(new S2CPlayerJoinPacket(player, Static.DEBUG || game.getOnlinePlayers().size() >= Game.MIN_PLAYERS));
        } catch (PacketSendException e) {
            if (DEBUG) LitLogger.error(e.getMessage(), e);
        }

        if (DEBUG) LitLogger.info(client + " joined game with code '" + game.getCode() + "'");
    }

    @Override
    public void handleDisconnect(C2SDisconnectPacket packet) {
        if (client == null) {
            lit.netSocket().removeClient(netHandler);
            return;
        }

        lit.netSocket().removeClient(client);

        ServerGame game = lit.getGame(client);
        if (game != null) game.playerDisconnected(client);

        LitLogger.info(client + " disconnected. Reason: " + packet.getReason().name()); //TODO Change enum name to something more readable
    }

    @Override
    public void handleStartGame(C2SStartGamePacket packet) {
        if (client == null) {
            if (DEBUG) LitLogger.error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerGame game = lit.getGame(client);
        if (game == null) {
            if (DEBUG) LitLogger.warn(client + " tried to start a game he is not in");
            return;
        }

        ServerPlayer player = game.getPlayer(client.getUniqueId());
        if (player == null) {
            if (DEBUG) LitLogger.warn(client + " tried to start a game he is not in");
            return;
        }

        if (!player.isMaster()) {
            if (DEBUG) LitLogger.warn(client + " tried to start a game he is not the master of");
            return;
        }

        if (!Static.DEBUG && game.getPlayerCount() < Game.MIN_PLAYERS) {
            if (DEBUG)
                LitLogger.warn(client + " tried to start a game with less than " + Game.MIN_PLAYERS + " players");

            netHandler.sendPacket(new S2CModalPacket("insufficient_players", Text.MODAL_TITLE_INSUFFICIENT_PLAYERS.getText(), Text.MODAL_BODY_INSUFFICIENT_PLAYERS.getText()));

            return;
        }

        game.start();
    }

    @Override
    public void handleSelect(C2SSelectPacket packet) {
        if (client == null) {
            if (DEBUG) LitLogger.error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerGame game = lit.getGame(client);
        if (game == null) {
            if (DEBUG) LitLogger.warn(client + " select a player while not in a game");
            return;
        }

        ServerPlayer player = game.getPlayer(client.getUniqueId());
        if (player == null) {
            if (DEBUG) LitLogger.warn(client + " select a player while not in a game");
            return;
        }

        ServerPlayer target = game.getPlayer(packet.getUuid());
        if (target == null) {
            if (DEBUG) LitLogger.warn(client + " tried to select a non-existent player");
            return;
        }

        game.selectTarget(target);

        try {
            game.notify();
        } catch (IllegalMonitorStateException e) {
            LitLogger.warn(e.getMessage());
        }
    }

    private void disconnect(@NotNull DisconnectReason reason) throws PacketSendException {
        netHandler.sendPacket(new S2CDisconnectPacket(reason));

        if (client != null) lit.netSocket().removeClient(client);
        else lit.netSocket().removeClient(netHandler);
    }
}

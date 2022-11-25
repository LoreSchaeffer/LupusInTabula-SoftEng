package it.multicoredev.server.network;

import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.enums.Message;
import it.multicoredev.enums.SceneId;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.network.IServerPacketListener;
import it.multicoredev.network.clientbound.S2CAlertPacket;
import it.multicoredev.network.clientbound.S2CChangeScenePacket;
import it.multicoredev.network.clientbound.S2CDisconnectPacket;
import it.multicoredev.network.clientbound.S2CHandshakePacket;
import it.multicoredev.network.serverbound.*;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.server.models.ServerGame;
import it.multicoredev.server.models.ServerPlayer;
import it.multicoredev.utils.LitLogger;
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
            LitLogger.get().error(e.getMessage(), e);
            //TODO Manage exception
        }

        if (DEBUG)
            LitLogger.get().info("Client '" + packet.getUsername() + "' (" + (newClientId != null ? newClientId : packet.getClientId()) + ") connected");
    }

    //TODO
    @Override
    public void handleMessage(C2SMessagePacket packet) {
        if (client == null) {
            if (DEBUG) LitLogger.get().error("The client did not perform the handshake. Packet ignored");
            return;
        }

        //TODO Handle message
        LitLogger.get().info("MSG: " + packet.getMessage());
    }

    @Override
    public void handleCreateGame(C2SCreateGame packet) {
        if (client == null) {
            if (DEBUG) LitLogger.get().error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerPlayer player = new ServerPlayer(client, true, netHandler);
        Game game = lit.createGame(player);

        try {
            netHandler.sendPacket(new S2CChangeScenePacket(SceneId.LOBBY));
        } catch (PacketSendException e) {
            if (DEBUG) LitLogger.get().error(e.getMessage(), e);
            return;
        }

        if (DEBUG) LitLogger.get().info(client + " created game with code '" + game.getCode() + "'");
    }

    @Override
    public void handleJoinGame(C2SJoinGame packet) {
        if (client == null) {
            if (DEBUG) LitLogger.get().error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerGame game = lit.getGame(packet.getCode());

        if (game == null) {
            if (DEBUG) LitLogger.get().info(client + " tried to join a non-existent game with code '" + packet.getCode() + "'");

            try {
                disconnect(DisconnectReason.S2C_GAME_NOT_FOUND);
            } catch (PacketSendException e) {
                if (DEBUG) LitLogger.get().error(e.getMessage(), e);
            }

            return;
        }

        ServerPlayer player = new ServerPlayer(client, false, netHandler);
        game.addPlayer(player);

        try {
            netHandler.sendPacket(new S2CChangeScenePacket(SceneId.LOBBY));
        } catch (PacketSendException e) {
            if (DEBUG) LitLogger.get().error(e.getMessage(), e);
            return;
        }

        if (DEBUG) LitLogger.get().info(client + " joined game with code '" + game.getCode() + "'");
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

        LitLogger.get().info(client + " disconnected. Reason: " + packet.getReason().name()); //TODO Change enum name to something more readable
    }

    @Override
    public void handleStartGame(C2SStartGamePacket packet) {
        if (client == null) {
            if (DEBUG) LitLogger.get().error("The client did not perform the handshake. Packet ignored");
            return;
        }

        ServerGame game = lit.getGame(packet.getCode());
        if (game == null) {
            if (DEBUG) LitLogger.get().warn(client + "tried to start a non-existent game with code '" + packet.getCode() + "'");
            return;
        }

        ServerPlayer player = game.getPlayer(client.getUniqueId());
        if (player == null) {
            if (DEBUG) LitLogger.get().warn(client + "tried to start a game he is not in");
            return;
        }

        if (!player.isMaster()) {
            if (DEBUG) LitLogger.get().warn(client + "tried to start a game he is not the master of");
            return;
        }

        if (game.getPlayerCount() < Game.MIN_PLAYERS) {
            if (DEBUG) LitLogger.get().warn(client + "tried to start a game with less than " + Game.MIN_PLAYERS + " players");

            netHandler.sendPacket(new S2CAlertPacket(Message.INSUFFICIENT_PLAYERS));

            return;
        }

        game.start();
    }

    private void disconnect(@NotNull DisconnectReason reason) throws PacketSendException {
        netHandler.sendPacket(new S2CDisconnectPacket(reason));

        if (client != null) lit.netSocket().removeClient(client);
        else lit.netSocket().removeClient(netHandler);
    }
}

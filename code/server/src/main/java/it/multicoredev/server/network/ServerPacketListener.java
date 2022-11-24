package it.multicoredev.server.network;

import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.models.Client;
import it.multicoredev.models.Game;
import it.multicoredev.network.IServerPacketListener;
import it.multicoredev.network.clientbound.S2CHandshakePacket;
import it.multicoredev.network.serverbound.*;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.server.models.ServerGame;
import it.multicoredev.server.models.ServerPlayer;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;

import java.util.UUID;

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
        //TODO Check if the client is allowed to connect

        ServerNetSocket netSocket = lit.getNetSocket();

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

        if (Static.DEBUG)
            LitLogger.get().info("Client '" + packet.getUsername() + "' (" + (newClientId != null ? newClientId : packet.getClientId()) + ") connected");
    }

    @Override
    public void handleMessage(C2SMessagePacket packet) {
        //TODO Handle message
        LitLogger.get().info("MSG: " + packet.getMessage());
    }

    @Override
    public void handleCreateGame(C2SCreateGame packet) {
        if (client == null) {
            LitLogger.get().error("Client not found");
            //TODO Manage exception
            return;
        }

        ServerPlayer player = new ServerPlayer(client, true, netHandler);
        Game game = lit.createGame(player);

        if (Static.DEBUG)
            LitLogger.get().info(client.getName() + " (" + client.getUniqueId() + ") created game with code " + game.getCode());
    }

    @Override
    public void handleJoinGame(C2SJoinGame packet) {
        ServerGame game = lit.getGame(packet.getCode());

        if (game == null) {
            //TODO Send game not found packet
            return;
        }

        if (client == null) {
            LitLogger.get().error("Client not found");
            //TODO Manage exception
            return;
        }

        ServerPlayer player = new ServerPlayer(client, false, netHandler);
        game.addPlayer(player);

        if (Static.DEBUG)
            LitLogger.get().info(client.getName() + " (" + client.getUniqueId() + ") joined game with code " + game.getCode());
    }

    @Override
    public void handleDisconnect(C2SDisconnectPacket packet) {
        if (client == null) {
            LitLogger.get().error("Client not found");
            //TODO Manage exception
            return;
        }

        lit.getNetSocket().removeClient(client);
        //TODO Remove player from games

        if (Static.DEBUG) LitLogger.get().info(client.getName() + " (" + client.getUniqueId() + ") disconnected");
    }

    @Override
    public void handleStartGame(C2SStartGamePacket packet) {
        ServerGame game = lit.getGame(packet.getCode());
        if (game == null) {
            LitLogger.get().error("Game not found");
            //TODO Manage exception
            return;
        }

        if (client == null) {
            LitLogger.get().error("Client not found");
            //TODO Manage exception
            return;
        }

        ServerPlayer player = game.getPlayer(client.getUniqueId());
        if (player == null) {
            LitLogger.get().error("Player not found");
            //TODO Manage exception
            return;
        }

        if (!player.isMaster()) return;

        game.start();
    }
}

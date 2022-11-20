package it.multicoredev.server.network;

import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.network.IServerPacketListener;
import it.multicoredev.network.clientbound.S2CHandshakePacket;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SMessagePacket;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;

import java.util.UUID;

public class ServerPacketListener implements IServerPacketListener {
    private ServerNetHandler netHandler;

    @Override
    public void setNetworkHandler(NetworkHandler handler) {
        if (netHandler != null) throw new IllegalStateException("Network handler already set");
        netHandler = (ServerNetHandler) handler;
    }

    @Override
    public void handleHandshake(C2SHandshakePacket packet) {
        //TODO Check if the client is allowed to connect

        ServerNetSocket netSocket = LupusInTabula.get().getNetSocket();

        UUID newClientId = null;
        if (netSocket.clientExists(packet.getClientId())) newClientId = netSocket.getNewClientId();

        netSocket.addClient(newClientId != null ? newClientId : packet.getClientId(), netHandler);

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
}

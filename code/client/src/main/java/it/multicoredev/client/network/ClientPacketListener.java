package it.multicoredev.client.network;

import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;

public class ClientPacketListener implements IClientPacketListener {
    private final ClientNetSocket netSocket;
    private ClientNetHandler netHandler;

    public ClientPacketListener(ClientNetSocket netSocket) {
        this.netSocket = netSocket;
    }

    @Override
    public void setNetworkHandler(NetworkHandler handler) {
        if (netHandler != null) throw new IllegalStateException("Network handler already set");
        netHandler = (ClientNetHandler) handler;
    }

    @Override
    public void handleHandshake(S2CHandshakePacket packet) {
        if (!packet.isClientAccepted()) {
            netSocket.disconnect();
            LitLogger.get().info("Server rejected the connection: " + packet.getReason());

            //TODO Show connection refused alert
            return;
        }

        if (packet.getNewClientId() != null) {
            netSocket.changeId(packet.getNewClientId());
            if (Static.DEBUG) LitLogger.get().info("Client id changed to " + packet.getNewClientId());
        }
    }

    @Override
    public void handleDisconnect(S2CDisconnectPacket packet) {
        netSocket.disconnect();
        LitLogger.get().info("Disconnected from server: " + packet.getReason()); //TODO Change to a more readable form

        //TODO Handle disconnection
    }

    @Override
    public void handleMessage(S2CMessagePacket packet) {
        LitLogger.get().info("CHAT: " + packet.getSender() + " > " + packet.getMessage());
    }

    @Override
    public void handleChangeScene(S2CChangeScenePacket packet) {
        //TODO Handle scene change
    }

    @Override
    public void handleCountdown(S2CGameStartCountdownPacket packet) {
        if (Static.DEBUG) LitLogger.get().info("Game starting in " + packet.getSeconds() + " seconds");
        //TODO Handle countdown
    }

    @Override
    public void handleAlert(S2CAlertPacket packet) {
        LitLogger.get().info("ALERT: " + packet.getMessage().getId());
        //TODO Handle alert
    }

    @Override
    public void handleGame(S2CGamePacket packet) {
        //TODO Handle game
        LitLogger.get().info(Static.GSON.toJson(packet.getGame()));
    }

    @Override
    public void handleTime(S2CTimePacket packet) {

    }
}

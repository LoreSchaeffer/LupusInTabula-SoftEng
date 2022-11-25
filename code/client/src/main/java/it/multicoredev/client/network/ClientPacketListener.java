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
            if (Static.DEBUG) LitLogger.get().info("Server rejected the connection: " + packet.getReason());

            //TODO Handle disconnection

            return;
        }

        if (packet.getNewClientId() != null) {
            netSocket.changeId(packet.getNewClientId());
            if (Static.DEBUG) LitLogger.get().info("Client id changed to " + packet.getNewClientId());
        }
    }

    @Override
    public void handleDisconnect(S2CDisconnectPacket packet) {

    }

    @Override
    public void handleMessage(S2CMessagePacket packet) {

    }

    @Override
    public void handleChangeScene(S2CChangeScenePacket packet) {

    }

    @Override
    public void handleCountdown(S2CGameStartCountdownPacket packet) {

    }

    @Override
    public void handleAlert(S2CAlertPacket packet) {

    }
}

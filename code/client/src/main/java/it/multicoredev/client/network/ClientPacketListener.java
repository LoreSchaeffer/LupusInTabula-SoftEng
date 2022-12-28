package it.multicoredev.client.network;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;

public class ClientPacketListener implements IClientPacketListener {
    private final ClientNetSocket net;
    private final LupusInTabula lit;
    private ClientNetHandler netHandler;

    public ClientPacketListener(ClientNetSocket net) {
        this.net = net;
        this.lit = LupusInTabula.get();
    }

    @Override
    public void setNetworkHandler(NetworkHandler handler) {
        if (netHandler != null) throw new IllegalStateException("Network handler already set");
        netHandler = (ClientNetHandler) handler;
    }

    @Override
    public void handleHandshake(S2CHandshakePacket packet) {
        if (!packet.isClientAccepted()) {
            net.disconnect();
            LitLogger.get().info("Server rejected the connection: " + packet.getReason());

            lit.showModal("connection_rejected", "<h1>Connection rejected</h1><p>" + packet.getReason() + "</p>"); //TODO Change message
            return;
        }

        if (packet.getNewClientId() != null) {
            net.changeId(packet.getNewClientId());
            if (Static.DEBUG) LitLogger.get().info("Client id changed to " + packet.getNewClientId());
        }

        net.setHandshakeDone();
    }

    @Override
    public void handleDisconnect(S2CDisconnectPacket packet) {
        net.disconnect(true);
        LitLogger.get().info("Disconnected from server: " + packet.getReason()); //TODO Change to a more readable form

        if (packet.getReason().equals(DisconnectReason.S2C_GAME_NOT_FOUND)) {
            lit.showModal("game_not_found", "<h1>Game not found</h1><p>The game you tried to join does not exist.</p>"); //TODO Change message
        }

        //TODO Handle disconnection
    }

    @Override
    public void handleMessage(S2CMessagePacket packet) {
        LitLogger.get().info("CHAT: " + packet.getSender() + " > " + packet.getMessage());
    }

    @Override
    public void handleChangeScene(S2CChangeScenePacket packet) {
        lit.setScene(packet.getScene());
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

    @Override
    public void handleTimer(S2CTimerPacket packet) {

    }

    @Override
    public void handleGameCreated(S2CGameCreatedPacket packet) {
        LitLogger.get().info("Game created: " + packet.getGame().getCode());
        lit.setCurrentGame(packet.getGame());
    }
}

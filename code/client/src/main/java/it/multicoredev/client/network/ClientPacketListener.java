package it.multicoredev.client.network;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.comms.messages.b2f.GameStartCountdownMessage;
import it.multicoredev.client.ui.comms.messages.b2f.PlayerJoinMessage;
import it.multicoredev.client.ui.comms.messages.b2f.PlayerLeaveMessage;
import it.multicoredev.client.ui.comms.messages.b2f.ReadyToStartMessage;
import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.models.Player;
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

            lit.showModal("connection_rejected", "Connection rejected", packet.getReason()); //TODO Localize
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
            lit.showModal("game_not_found", "Game not found", "The game you tried to join does not exists"); //TODO Localize
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
        lit.executeFrontendCode(new GameStartCountdownMessage(packet.getSeconds()));
        if (Static.DEBUG) LitLogger.get().info("Game starting in " + packet.getSeconds() + " seconds");
    }

    @Override
    public void handleModal(S2CModalPacket packet) {
        lit.showModal(packet.getId(), packet.getTitle().getPath(), packet.getBody().getPath(), packet.isLarge(), false); //TODO Localize
    }

    @Override
    public void handleGame(S2CGamePacket packet) {
        lit.setCurrentGame(packet.getGame());
        if (Static.DEBUG) LitLogger.get().info(Static.GSON.toJson(packet.getGame()));
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

    @Override
    public void handleGameJoined(S2CGameJoinedPacket packet) {
        LitLogger.get().info("Game joined: " + packet.getGame().getCode());
        lit.setCurrentGame(packet.getGame());
    }

    @Override
    public void handlePlayerJoin(S2CPlayerJoinPacket packet) {
        lit.getCurrentGame().addPlayer(packet.getPlayer());
        lit.executeFrontendCode(new PlayerJoinMessage(packet.getPlayer()));
        if (packet.isReadyToStart() && lit.getCurrentGame().getPlayer(lit.getClientId()).isMaster()) lit.executeFrontendCode(new ReadyToStartMessage(true));

        LitLogger.get().info("Player joined: " + packet.getPlayer().getName());
    }

    @Override
    public void handlePlayerLeave(S2CPlayerLeavePacket packet) {
        if (lit.getCurrentGame() == null) return;

        Player player = lit.getCurrentGame().getPlayer(packet.getClientId());
        if (player != null) lit.getCurrentGame().removePlayer(player);
        lit.executeFrontendCode(new PlayerLeaveMessage(packet.getClientId()));
        if (!packet.isReadyToStart() && lit.getCurrentGame().getPlayer(lit.getClientId()).isMaster()) lit.executeFrontendCode(new ReadyToStartMessage(false));

        if (player != null) LitLogger.get().info("Player left: " + player.getName());
        else LitLogger.get().info("Player left: " + packet.getClientId());
    }
}

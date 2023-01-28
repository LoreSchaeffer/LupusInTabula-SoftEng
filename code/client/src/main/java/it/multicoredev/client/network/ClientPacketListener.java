package it.multicoredev.client.network;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.assets.Locale;
import it.multicoredev.client.ui.comms.messages.b2f.*;
import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.models.Player;
import it.multicoredev.network.IClientPacketListener;
import it.multicoredev.network.clientbound.*;
import it.multicoredev.text.BaseText;
import it.multicoredev.text.StaticText;
import it.multicoredev.text.Text;
import it.multicoredev.text.TranslatableText;
import it.multicoredev.utils.Encryption;
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
            LitLogger.info("Server rejected the connection: " + packet.getReason());
            lit.showModal("connection_rejected", Text.MODAL_TITLE_CONNECTION_REJECTED.getText(), Text.MODAL_BODY_CONNECTION_REJECTED.getText());
            return;
        }

        if (packet.getNewClientId() != null) {
            net.changeId(packet.getNewClientId());
            if (Static.DEBUG) LitLogger.info("Client id changed to " + packet.getNewClientId());
        }

        Encryption.setSecret(packet.getSecret());

        net.setHandshakeDone();
    }

    @Override
    public void handleDisconnect(S2CDisconnectPacket packet) {
        net.disconnect(true);
        LitLogger.info("Disconnected from server: " + packet.getReason());

        if (packet.getReason().equals(DisconnectReason.S2C_GAME_NOT_FOUND)) {
            lit.showModal("game_not_found", Text.MODAL_TITLE_GAME_NOT_FOUND.getText(), Text.MODAL_BODY_GAME_NOT_FOUND.getText());
        } else if (packet.getReason().equals(DisconnectReason.S2C_SERVER_CLOSING)) {
            lit.showModal("server_closing", Text.MODAL_TITLE_SERVER_CLOSING.getText(), Text.MODAL_BODY_SERVER_CLOSING.getText());
        } else {
            lit.showModal("generic_disconnection", Text.MODAL_TITLE_GENERIC_DISCONNECTION.getText(), Text.MODAL_BODY_GENERIC_DISCONNECTION.getText());
        }
    }

    @Override
    public void handleMessage(S2CMessagePacket packet) {
        lit.sendChatMessage(packet.getSender(), packet.getMessage(), packet.getChannel());
    }

    @Override
    public void handleChangeScene(S2CChangeScenePacket packet) {
        lit.setScene(packet.getScene());
    }

    @Override
    public void handleCountdown(S2CGameStartCountdownPacket packet) {
        lit.executeFrontendCode(new GameStartCountdownMessage(packet.getSeconds()));
        if (Static.DEBUG) LitLogger.info("Game starting in " + packet.getSeconds() + " seconds");
    }

    @Override
    public void handleModal(S2CModalPacket packet) {
        Locale locale = lit.getLocale();

        BaseText title = packet.getTitle();
        BaseText body = packet.getBody();

        lit.showModal(
                packet.getId(),
                title instanceof StaticText ? title.getText() : ((TranslatableText) title).setLocalization(locale).getText(),
                body instanceof StaticText ? body.getText() : ((TranslatableText) body).setLocalization(locale).getText(),
                packet.isLarge(),
                false
        );
    }

    @Override
    public void handleGame(S2CGamePacket packet) {
        lit.setCurrentGame(packet.getGame());
        lit.executeFrontendCode(new GameUpdateMessage(packet.getGame()));
        if (packet.shouldUpdateUI()) packet.getGame().getPlayers().forEach(player -> lit.executeFrontendCode(new UpdatePlayerMessage(player, null)));

        if (Static.DEBUG) LitLogger.info(Static.GSON.toJson(packet.getGame()));
    }

    @Override
    public void handleTimer(S2CTimerPacket packet) {
        lit.executeFrontendCode(new TimerMessage(packet.getTime()));
    }

    @Override
    public void handleGameCreated(S2CGameCreatedPacket packet) {
        LitLogger.info("Game created: " + packet.getGame().getCode());
        lit.setCurrentGame(packet.getGame());
    }

    @Override
    public void handleGameJoined(S2CGameJoinedPacket packet) {
        LitLogger.info("Game joined: " + packet.getGame().getCode());
        lit.setCurrentGame(packet.getGame());
    }

    @Override
    public void handlePlayerJoin(S2CPlayerJoinPacket packet) {
        lit.getCurrentGame().addPlayer(packet.getPlayer());
        lit.executeFrontendCode(new PlayerJoinMessage(packet.getPlayer()));
        if (packet.isReadyToStart() && lit.getCurrentGame().getPlayer(lit.getClientId()).isMaster())
            lit.executeFrontendCode(new ReadyToStartMessage(true));

        LitLogger.info("Player joined: " + packet.getPlayer().getName());
    }

    @Override
    public void handleTurn(S2CTurnPacket packet) {
        Player player = lit.getPlayer();
        if (player == null) return;

        lit.executeFrontendCode(new UpdatePlayerMessage(player, packet.isTurnStart()));
    }

    @Override
    public void handleEndGame(S2CEndGamePacket packet) {
        lit.setWinner(packet.getWinner());
    }

    @Override
    public void handlePlayerLeave(S2CPlayerLeavePacket packet) {
        if (lit.getCurrentGame() == null) return;

        Player player = lit.getCurrentGame().getPlayer(packet.getClientId());
        if (player != null) lit.getCurrentGame().removePlayer(player);
        lit.executeFrontendCode(new PlayerLeaveMessage(packet.getClientId()));
        if (!packet.isReadyToStart() && lit.getCurrentGame().getPlayer(lit.getClientId()).isMaster())
            lit.executeFrontendCode(new ReadyToStartMessage(false));

        if (player != null) LitLogger.info("Player left: " + player.getName());
        else LitLogger.info("Player left: " + packet.getClientId());
    }
}

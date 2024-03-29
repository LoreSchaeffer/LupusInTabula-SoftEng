package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.clientbound.*;

public interface IClientPacketListener extends PacketListener {

    void handleHandshake(S2CHandshakePacket packet);

    void handleDisconnect(S2CDisconnectPacket packet);

    void handleMessage(S2CMessagePacket packet);

    void handleChangeScene(S2CChangeScenePacket packet);

    void handleCountdown(S2CGameStartCountdownPacket packet);

    void handleModal(S2CModalPacket packet);

    void handleGame(S2CGamePacket packet);

    void handleTimer(S2CTimerPacket packet);

    void handleGameCreated(S2CGameCreatedPacket packet);

    void handlePlayerLeave(S2CPlayerLeavePacket packet);

    void handleGameJoined(S2CGameJoinedPacket packet);

    void handlePlayerJoin(S2CPlayerJoinPacket packet);

    void handleTurn(S2CTurnPacket packet);

    void handleEndGame(S2CEndGamePacket packet);
}

package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.clientbound.*;

public interface IClientPacketListener extends PacketListener {

    void handleHandshake(S2CHandshakePacket packet);

    void handleDisconnect(S2CDisconnectPacket packet);

    void handleMessage(S2CMessagePacket packet);

    void handleChangeScene(S2CChangeScenePacket packet);

    void handleCountdown(S2CGameStartCountdownPacket packet);

    void handleAlert(S2CAlertPacket packet);

    void handleGame(S2CGamePacket packet);

    void handleTime(S2CTimePacket packet);

    void handleTimer(S2CTimerPacket packet);

    void handleGameCreated(S2CGameCreatedPacket packet);

    void handlePlayerLeave(S2CPlayerLeavePacket packet);

    void handleGameJoined(S2CGameJoinedPacket packet);

    void handlePlayerJoin(S2CPlayerJoinPacket packet);
}

package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.clientbound.*;

public interface IClientPacketListener extends PacketListener {

    void handleHandshake(S2CHandshakePacket packet);

    void handleDisconnect(S2CDisconnectPacket packet);

    void handleMessage(S2CMessagePacket packet);

    void handleChangeScene(S2CChangeScenePacket packet);

    void handleCountdown(S2CCountdownPacket packet);
}

package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.clientbound.S2CChangeScenePacket;
import it.multicoredev.network.clientbound.S2CDisconnectPacket;
import it.multicoredev.network.clientbound.S2CHandshakePacket;
import it.multicoredev.network.clientbound.S2CMessagePacket;

public interface IClientPacketListener extends PacketListener {

    void handleHandshake(S2CHandshakePacket packet);

    void handleDisconnect(S2CDisconnectPacket packet);

    void handleMessage(S2CMessagePacket packet);

    void handleChangeScene(S2CChangeScenePacket packet);
}

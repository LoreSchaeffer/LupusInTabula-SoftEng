package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SMessagePacket;

public interface IServerPacketListener extends PacketListener {

    void handleHandshake(C2SHandshakePacket packet);

    void handleMessage(C2SMessagePacket packet);
}

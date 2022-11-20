package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.clientbound.S2CHandshakePacket;

public interface IClientPacketListener extends PacketListener {

    void handleHandshake(S2CHandshakePacket packet);
}

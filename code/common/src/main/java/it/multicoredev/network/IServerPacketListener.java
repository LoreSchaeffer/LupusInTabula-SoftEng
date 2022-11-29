package it.multicoredev.network;

import it.multicoredev.mclib.network.protocol.PacketListener;
import it.multicoredev.network.serverbound.*;

public interface IServerPacketListener extends PacketListener {

    void handleHandshake(C2SHandshakePacket packet);

    void handleMessage(C2SMessagePacket packet);

    void handleCreateGame(C2SCreateGame packet);

    void handleJoinGame(C2SJoinGamePacket packet);

    void handleDisconnect(C2SDisconnectPacket packet);

    void handleStartGame(C2SStartGamePacket packet);
}

package it.multicoredev.server.network;

import it.multicoredev.mclib.network.protocol.PacketListener;

public class ServerPacketListener implements PacketListener {
    private final ServerNetSocket netSocket;

    public ServerPacketListener(ServerNetSocket netSocket) {
        this.netSocket = netSocket;
    }
}

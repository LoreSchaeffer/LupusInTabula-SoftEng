package it.multicoredev.client.network;

import it.multicoredev.mclib.network.protocol.PacketListener;

public class ClientPacketListener implements PacketListener {
    private final ClientNetSocket netSocket;

    public ClientPacketListener(ClientNetSocket netSocket) {
        this.netSocket = netSocket;
    }
}

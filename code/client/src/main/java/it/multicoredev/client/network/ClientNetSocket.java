package it.multicoredev.client.network;

import it.multicoredev.mclib.network.client.ClientSocket;
import it.multicoredev.mclib.network.client.ServerAddress;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.Packets;
import it.multicoredev.utils.LitLogger;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClientNetSocket {
    private ClientSocket socket;
    private Thread connectionThread;
    private UUID clientId;

    public ClientNetSocket() {
        clientId = UUID.randomUUID();
        Packets.registerPackets();
    }

    public void connect(@NotNull ServerAddress address) {
        socket = new ClientSocket(address, new ClientNetHandler(new ClientPacketListener(this)));

        connectionThread = new Thread(() -> {
            try {
                socket.connect();
            } catch (InterruptedException e) {
                LitLogger.get().error(e.getMessage(), e);
                disconnect();
            }
        });
        connectionThread.start();
    }

    public void disconnect() {
        if (connectionThread != null) {
            if (socket != null) {
                if (socket.isConnected()) {
                    //TODO Send disconnect packet
                    //TODO Add disconnect method
                }

                socket = null;
            }

            connectionThread.interrupt();
            connectionThread = null;
        }
    }

    public void sendPacket(Packet<?> packet) {
        socket.sendPacket(packet);
    }

    ClientSocket getSocket() {
        return socket;
    }

    void changeId(UUID newClientId) {
        this.clientId = newClientId;
    }
}

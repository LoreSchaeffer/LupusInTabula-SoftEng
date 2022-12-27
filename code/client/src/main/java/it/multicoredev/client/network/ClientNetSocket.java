package it.multicoredev.client.network;

import it.multicoredev.mclib.network.client.ClientSocket;
import it.multicoredev.mclib.network.client.ServerAddress;
import it.multicoredev.mclib.network.exceptions.PacketSendException;
import it.multicoredev.mclib.network.protocol.Packet;
import it.multicoredev.network.Packets;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClientNetSocket {
    private ClientSocket socket;
    private Thread connectionThread;
    private UUID clientId;
    private boolean handshakeDone = false;

    public ClientNetSocket() {
        clientId = UUID.randomUUID();
        Packets.registerPackets();
    }

    public void connect(@NotNull ServerAddress address) {
        socket = new ClientSocket(address, new ClientNetHandler(), new ClientPacketListener(this));

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
                    socket.disconnect();

                    //TODO To test
                    while(socket.isConnected()) {
                        Utils.sleep(10);
                    }
                }

                socket = null;
            }

            connectionThread.interrupt();
            connectionThread = null;
        }

        handshakeDone = false;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setHandshakeDone() {
        handshakeDone = true;
    }

    public boolean isHandshakeDone() {
        return handshakeDone;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void sendPacket(Packet<?> packet) throws PacketSendException {
        socket.sendPacket(packet);
    }

    void changeId(UUID newClientId) {
        this.clientId = newClientId;
    }
}

package it.multicoredev.server.network;

import it.multicoredev.mclib.network.server.ServerSocket;
import it.multicoredev.network.Packets;
import it.multicoredev.utils.LitLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerNetSocket {
    private final ServerSocket socket;
    private Thread connectionThread;
    private final Map<UUID, ServerNetHandler> clients = new HashMap<>();

    public ServerNetSocket(int port) {
        Packets.registerPackets();

        socket = new ServerSocket(port, ServerPacketListener.class, ServerNetHandler.class);
    }

    public void start() {
        connectionThread = new Thread(() -> {
            try {
                socket.startServer();
            } catch (InterruptedException e) {
                LitLogger.get().error(e.getMessage(), e);
                stop();
            }
        });
        connectionThread.start();
    }

    public void stop() {
        if (connectionThread != null) {
            //TODO Send disconnect packet to all clients
            //TODO Disconnect all clients
            socket.stopServer();

            connectionThread.interrupt();
            connectionThread = null;
        }
    }

    void addClient(UUID clientId, ServerNetHandler netHandler) {
        clients.put(clientId, netHandler);
    }

    void removeClient(UUID clientId) {
        clients.remove(clientId);
    }

    boolean clientExists(UUID clientId) {
        return clients.containsKey(clientId);
    }

    UUID getNewClientId() {
        UUID newId;
        do {
            newId = UUID.randomUUID();
        } while (clientExists(newId));

        return newId;
    }
}

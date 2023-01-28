package it.multicoredev.server.network;

import it.multicoredev.enums.DisconnectReason;
import it.multicoredev.mclib.network.server.ServerSocket;
import it.multicoredev.models.Client;
import it.multicoredev.network.Packets;
import it.multicoredev.network.clientbound.S2CDisconnectPacket;
import it.multicoredev.server.LupusInTabula;
import it.multicoredev.utils.LitLogger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerNetSocket {
    private final ServerSocket socket;
    private Thread connectionThread;
    private final Map<Client, ServerNetHandler> clients = new HashMap<>();

    public ServerNetSocket(int port) {
        Packets.registerPackets();

        socket = new ServerSocket(port, ServerPacketListener.class, ServerNetHandler.class);
    }

    public void start() {
        connectionThread = new Thread(() -> {
            try {
                socket.startServer();
            } catch (InterruptedException e) {
                LitLogger.error(e.getMessage(), e);
                stop();
            }
        });
        connectionThread.start();
    }

    public void stop() {
        if (connectionThread != null) {
            clients.forEach((client, netHandler) -> {
                netHandler.sendPacket(new S2CDisconnectPacket(DisconnectReason.S2C_SERVER_CLOSING));
                netHandler.disconnect();
            });
            clients.clear();

            socket.stopServer();

            connectionThread.interrupt();
            connectionThread = null;
        }
    }

    void addClient(Client client, ServerNetHandler netHandler) {
        clients.put(client, netHandler);
    }

    void removeClient(Client client) {
        clients.remove(client);
    }

    void removeClient(ServerNetHandler netHandler) {
        for (Map.Entry<Client, ServerNetHandler> client : clients.entrySet()) {
            if (client.getValue().equals(netHandler)) {
                clients.remove(client.getKey());
                break;
            }
        }
    }

    boolean clientExists(UUID clientId) {
        return clients.keySet().stream().filter(client -> client.getUniqueId().equals(clientId)).findFirst().orElse(null) != null;
    }

    public UUID getNewClientId() {
        UUID newId;
        do {
            newId = UUID.randomUUID();
        } while (clientExists(newId));

        return newId;
    }
}

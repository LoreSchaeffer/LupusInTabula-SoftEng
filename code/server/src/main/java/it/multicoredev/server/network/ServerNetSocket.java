package it.multicoredev.server.network;

import io.netty.handler.logging.LogLevel;
import it.multicoredev.mclib.network.server.ServerSocket;
import it.multicoredev.network.Packets;
import it.multicoredev.utils.LitLogger;

public class ServerNetSocket {
    private final ServerSocket socket;
    private final ServerPacketListener packetListener;
    private Thread connectionThread;

    public ServerNetSocket(int port) {
        Packets.registerPackets();
        packetListener = new ServerPacketListener(this);

        socket = new ServerSocket(port, packetListener, ServerNetHandler.class);
        socket.setLogLevel(LogLevel.WARN);
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
            //TODO Disconnect clients
            //TODO Add stopServer method

            connectionThread.interrupt();
            connectionThread = null;
        }
    }
}

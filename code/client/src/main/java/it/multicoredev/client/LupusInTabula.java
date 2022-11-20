package it.multicoredev.client;

import it.multicoredev.client.network.ClientNetSocket;
import it.multicoredev.mclib.network.client.ServerAddress;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.utils.LitLogger;

public class LupusInTabula {
    private final ClientNetSocket netSocket;
    private static LupusInTabula instance;

    public LupusInTabula() {
        netSocket = new ClientNetSocket();
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        netSocket.connect(new ServerAddress("127.0.0.1", 12987));

        LitLogger.get().info("Waiting for connection...");
        while (!netSocket.isConnected()) {
            try {
                Thread.sleep(500);
                System.out.printf(".");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LitLogger.get().info("Connected!");

        netSocket.sendPacket(new C2SHandshakePacket(netSocket.getClientId(), "Test"));
    }
}

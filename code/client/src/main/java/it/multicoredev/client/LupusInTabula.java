package it.multicoredev.client;

import it.multicoredev.client.assets.Config;
import it.multicoredev.client.network.ClientNetSocket;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.network.client.ServerAddress;
import it.multicoredev.network.serverbound.C2SHandshakePacket;
import it.multicoredev.network.serverbound.C2SMessagePacket;
import it.multicoredev.utils.LitLogger;

import java.io.File;

public class LupusInTabula {
    private static final GsonHelper GSON = new GsonHelper();
    private static LupusInTabula instance;

    private final ClientNetSocket netSocket;
    private final File confDir = new File("conf");
    private Config config;

    public LupusInTabula() {
        netSocket = new ClientNetSocket();
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        if (!confDir.exists() || !confDir.isDirectory()) {
            if (!confDir.mkdir()) {
                LitLogger.get().error("Could not create conf directory!");
                System.exit(1);
            }
        }

        try {
            config = GSON.autoload(new File("conf", "config.json"), new Config().init(), Config.class);
        } catch (Exception e) {
            LitLogger.get().error("Cannot create/load config file!", e);
            System.exit(2);
        }

        netSocket.connect(new ServerAddress(config.serverAddress, config.port));

        LitLogger.get().info("Waiting for connection...");
        while (!netSocket.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LitLogger.get().info("Connected!");

        netSocket.sendPacket(new C2SHandshakePacket(netSocket.getClientId(), "Test"));
        netSocket.sendPacket(new C2SMessagePacket("Hello world!"));
    }
}

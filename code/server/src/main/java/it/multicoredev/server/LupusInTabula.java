package it.multicoredev.server;

import it.multicoredev.server.network.ServerNetSocket;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class LupusInTabula {
    private final ServerNetSocket netSocket;
    private static LupusInTabula instance;

    private LupusInTabula() {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        netSocket = new ServerNetSocket(12987);
    }

    public static LupusInTabula get() {
        if (instance == null) instance = new LupusInTabula();
        return instance;
    }

    public void start() {
        netSocket.start();
    }

    public ServerNetSocket getNetSocket() {
        return netSocket;
    }
}

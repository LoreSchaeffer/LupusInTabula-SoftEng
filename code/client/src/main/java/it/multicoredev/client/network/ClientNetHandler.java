package it.multicoredev.client.network;

import io.netty.channel.ChannelHandlerContext;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.utils.LitLogger;

public class ClientNetHandler extends NetworkHandler {

    public ClientNetHandler(ClientPacketListener listener) {
        super(listener);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LitLogger.get().error(cause.getMessage(), cause);
    }
}

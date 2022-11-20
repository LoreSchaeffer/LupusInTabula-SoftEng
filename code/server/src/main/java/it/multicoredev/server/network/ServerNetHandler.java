package it.multicoredev.server.network;

import io.netty.channel.ChannelHandlerContext;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.utils.LitLogger;

public class ServerNetHandler extends NetworkHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LitLogger.get().error(cause.getMessage(), cause);
    }
}

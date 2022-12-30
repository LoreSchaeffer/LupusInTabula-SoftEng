package it.multicoredev.client.network;

import io.netty.channel.ChannelHandlerContext;
import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.utils.LitLogger;

public class ClientNetHandler extends NetworkHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LitLogger.get().error(cause.getMessage(), cause);
        LupusInTabula.get().setScene(Scene.MAIN_MENU);
        //TODO Send modal
    }
}

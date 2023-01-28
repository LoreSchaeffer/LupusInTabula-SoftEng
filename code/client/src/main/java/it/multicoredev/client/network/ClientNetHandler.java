package it.multicoredev.client.network;

import io.netty.channel.ChannelHandlerContext;
import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.mclib.network.NetworkHandler;
import it.multicoredev.text.Text;
import it.multicoredev.utils.LitLogger;

public class ClientNetHandler extends NetworkHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LitLogger.error(cause.getMessage(), cause);
        LupusInTabula.get().setScene(Scene.MAIN_MENU);
        LupusInTabula.get().showModal("network_exception", Text.MODAL_TITLE_NETWORK_EXCEPTION.getText(), Text.MODAL_BODY_NETWORK_EXCEPTION.getText(cause.getMessage()));
    }
}

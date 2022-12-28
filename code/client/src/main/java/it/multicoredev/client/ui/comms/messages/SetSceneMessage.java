package it.multicoredev.client.ui.comms.messages;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.ui.Gui;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.client.ui.comms.InternalMessage;
import it.multicoredev.utils.LitLogger;
import it.multicoredev.utils.Static;
import org.cef.callback.CefQueryCallback;
import org.jetbrains.annotations.NotNull;

public class SetSceneMessage extends InternalMessage {
    @SerializedName("scene")
    private final String sceneId;

    public SetSceneMessage(@NotNull String sceneId) {
        super("set_scene");
        this.sceneId = sceneId;
    }

    @Override
    public boolean process(LupusInTabula lit, Gui gui, CefQueryCallback callback) {
        if (sceneId == null || sceneId.trim().isEmpty()) {
            if (Static.DEBUG) LitLogger.get().error("Set scene request has no data");
            return false;
        }

        Scene scene = Scene.fromId(sceneId);
        if (scene == null) {
            if (Static.DEBUG) LitLogger.get().error("Set scene request has invalid scene id");
            return false;
        }

        gui.setScene(scene);
        return true;
    }
}

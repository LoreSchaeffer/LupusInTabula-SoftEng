package it.multicoredev.client.ui.comms;

import it.multicoredev.client.ui.comms.messages.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MessageRegistry {
    CLOSE(CloseMessage.class),
    GET_GAME_CODE(GetGameCodeMessage.class),
    GET_SELF(GetSelfMessage.class),
    JOIN_GAME(JoinGameMessage.class),
    NEW_GAME(NewGameMessage.class),
    SET_SCENE(SetSceneMessage.class),
    SET_USERNAME(SetUsernameMessage.class);

    private final Class<? extends InternalMessage> clazz;

    MessageRegistry(Class<? extends InternalMessage> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends InternalMessage> getType() {
        return clazz;
    }

    @Nullable
    public static Class<? extends InternalMessage> getType(@NotNull String type) {
        for (MessageRegistry registry : values()) {
            if (registry.name().toLowerCase().equals(type)) return registry.getType();
        }

        return null;
    }
}

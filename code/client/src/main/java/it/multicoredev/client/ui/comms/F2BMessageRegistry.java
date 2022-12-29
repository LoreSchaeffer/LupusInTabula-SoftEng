package it.multicoredev.client.ui.comms;

import it.multicoredev.client.ui.comms.messages.f2b.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum F2BMessageRegistry {
    CLOSE(CloseMessage.class),
    GET_GAME_CODE(GetGameCodeMessage.class),
    GET_PLAYERS(GetPlayersMessage.class),
    GET_SELF(GetSelfMessage.class),
    JOIN_GAME(JoinGameMessage.class),
    LEAVE_GAME(LeaveGameMessage.class),
    NEW_GAME(NewGameMessage.class),
    SET_SCENE(SetSceneMessage.class),
    SET_USERNAME(SetUsernameMessage.class);

    private final Class<? extends F2BMessage> clazz;

    F2BMessageRegistry(Class<? extends F2BMessage> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends F2BMessage> getType() {
        return clazz;
    }

    @Nullable
    public static Class<? extends F2BMessage> getType(@NotNull String type) {
        for (F2BMessageRegistry registry : values()) {
            if (registry.name().toLowerCase().equals(type)) return registry.getType();
        }

        return null;
    }
}

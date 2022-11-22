package it.multicoredev.network;

public enum DisconnectReason {
    S2C_GAME_NOT_FOUND,

    C2S_QUIT_GAME;

    public static DisconnectReason fromOrdinal(int ordinal) {
        for (DisconnectReason reason : values()) {
            if (reason.ordinal() == ordinal) return reason;
        }

        return null;
    }
}

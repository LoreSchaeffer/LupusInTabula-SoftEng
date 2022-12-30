package it.multicoredev.enums;

public enum Message {
    MODAL_TITLE_INSUFFICIENT_PLAYERS("modal.title.insufficient_players", "Insufficient players"),
    MODAL_BODY_INSUFFICIENT_PLAYERS("modal.body.insufficient_players", "You need at least %d players to start a game");

    private final String path;
    private final String defValue;

    Message(String id, String defValue) {
        this.path = id;
        this.defValue = defValue;
    }

    public String getPath() {
        return path;
    }

    public String getDefValue() {
        return defValue;
    }
}

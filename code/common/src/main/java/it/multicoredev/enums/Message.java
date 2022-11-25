package it.multicoredev.enums;

public enum Message {
    INSUFFICIENT_PLAYERS("alerts.insufficient_players");

    private final String id;

    Message(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

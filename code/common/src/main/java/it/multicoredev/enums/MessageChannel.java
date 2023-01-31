package it.multicoredev.enums;

public enum MessageChannel {
    ALL("all"),
    SYSTEM("system"),
    WEREWOLVES("werewolves"),
    SEERS("seers"),
    DEAD("dead");

    private final String id;

    MessageChannel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

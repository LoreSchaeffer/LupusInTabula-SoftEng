package it.multicoredev.models;

public enum SceneIds {
    BOOTSTRAP("bootstrap"),
    MAIN_MENU("main_menu"),
    LOBBY("lobby"),
    GAME("game"),
    ENDING("ending");

    private final String id;

    SceneIds(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

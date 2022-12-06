package it.multicoredev.enums;

public enum SceneId {
    BOOTSTRAP("bootstrap"),
    MAIN_MENU("main_menu"),
    LOBBY("lobby"),
    STARTING("starting"),
    GAME("game"),
    ENDING("ending"),
    SETTINGS("settings"),
    CREDITS("credits");

    private final String id;

    SceneId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

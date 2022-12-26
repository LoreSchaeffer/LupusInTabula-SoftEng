package it.multicoredev.client.ui;

import it.multicoredev.enums.SceneId;
import org.jetbrains.annotations.Nullable;

public enum Scene {
    BOOTSTRAP(SceneId.BOOTSTRAP, "local://assets/scenes/bootstrap/bootstrap.html"),
    MAIN_MENU(SceneId.MAIN_MENU, "local://assets/scenes/main_menu/main_menu.html"),
    LOBBY(SceneId.LOBBY, "local://assets/scenes/lobby/lobby.html"),
    STARTING(SceneId.STARTING, "local://assets/scenes/starting/starting.html"),
    GAME(SceneId.GAME, "local://assets/scenes/game/game.html"),
    ENDING(SceneId.ENDING, "local://assets/scenes/ending/ending.html"),
    SETTINGS(SceneId.SETTINGS, "local://assets/scenes/settings/settings.html"),
    HELP(SceneId.HELP, "local://assets/scenes/help/help.html");

    private final SceneId id;
    private final String url;

    Scene(SceneId id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public static Scene fromId(SceneId id) {
        for (Scene scene : values()) {
            if (scene.id.equals(id)) return scene;
        }

        return null;
    }

    @Nullable
    public static Scene fromId(String id) {
        for (Scene scene : values()) {
            if (scene.id.getId().equals(id)) return scene;
        }

        return null;
    }
}

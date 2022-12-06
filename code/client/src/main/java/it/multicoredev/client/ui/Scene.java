package it.multicoredev.client.ui;

import it.multicoredev.enums.SceneId;
import org.jetbrains.annotations.Nullable;

public enum Scene {
    BOOTSTRAP(SceneId.BOOTSTRAP, "local://scenes/bootstrap/bootstrap.html"),
    MAIN_MENU(SceneId.MAIN_MENU, "local://scenes/menu/menu.html"),
    LOBBY(SceneId.LOBBY, "local://scenes/lobby/lobby.html"),
    STARTING(SceneId.STARTING, "local://scenes/starting/starting.html"),
    GAME(SceneId.GAME, "local://scenes/game/game.html"),
    ENDING(SceneId.ENDING, "local://scenes/ending/ending.html"),
    SETTINGS(SceneId.SETTINGS, "local://scenes/settings/settings.html"),
    CREDITS(SceneId.CREDITS, "local://scenes/credits/credits.html");

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
            if (scene.id == id) return scene;
        }

        return null;
    }
}

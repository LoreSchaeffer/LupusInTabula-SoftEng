package it.multicoredev.client.ui.registries;

import it.multicoredev.client.exceptions.GraphicException;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.client.ui.scenes.BootstrapScene;
import it.multicoredev.enums.SceneId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Scenes {
    BOOTSTRAP(SceneId.BOOTSTRAP.getId(), BootstrapScene.class);

    private final String id;
    private final Class<? extends Scene> sceneClass;

    Scenes(String id, Class<? extends Scene> sceneClass) {
        this.id = id;
        this.sceneClass = sceneClass;
    }

    public String getId() {
        return id;
    }

    public Scene getInstance() throws GraphicException {
        try {
            return sceneClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new GraphicException("Unable to create scene instance of scene '" + id + "'", e);
        }
    }

    @Nullable
    public static Scenes getById(@NotNull String id) {
        for (Scenes scene : values()) {
            if (scene.getId().equals(id)) return scene;
        }

        return null;
    }
}

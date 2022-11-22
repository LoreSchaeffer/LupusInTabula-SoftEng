package it.multicoredev.client.ui.registries;

import it.multicoredev.client.exceptions.GraphicException;
import it.multicoredev.client.ui.Scene;
import it.multicoredev.client.ui.scenes.BootstrapScene;

public enum Scenes {
    BOOTSTRAP("bootstrap", BootstrapScene.class);

    private final String name;
    private final Class<? extends Scene> sceneClass;

    Scenes(String name, Class<? extends Scene> sceneClass) {
        this.name = name;
        this.sceneClass = sceneClass;
    }

    public String getName() {
        return name;
    }

    public Scene getInstance() throws GraphicException {
        try {
            return sceneClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new GraphicException("Unable to create scene instance of scene '" + name + "'", e);
        }
    }

}

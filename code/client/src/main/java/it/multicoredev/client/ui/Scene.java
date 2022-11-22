package it.multicoredev.client.ui;

public abstract class Scene {
    protected Camera camera;

    public void init() {
    }

    public void start() {
        //TODO
    }

    public abstract void update(float dt);

    public Camera camera() {
        return camera;
    }
}

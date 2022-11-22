package it.multicoredev.client.ui;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projection;
    private Matrix4f view;
    private Matrix4f inverseProjection;
    private Matrix4f inverseView;
    private Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);
    public Vector2f position;

    public Camera(Vector2f position) {
        this.position = position;
        this.projection = new Matrix4f();
        this.view = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    public Camera() {
        this(new Vector2f(0, 0));
    }

    public void adjustProjection() {
        projection.identity();
        projection.ortho(0.0f, projectionSize.x, 0.0f, projectionSize.y, 0.0f, 100.0f);
        projection.invert(inverseProjection);
    }

    public Matrix4f getView() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);

        view.identity();
        view.lookAt(new Vector3f(position.x, position.y, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);

        view.invert(inverseView);

        return view;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }
}

package it.multicoredev.client.ui.listeners;

import it.multicoredev.client.ui.Camera;
import it.multicoredev.client.ui.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX = 0;
    private double scrollY = 0;
    private double xPos = 0;
    private double yPos = 0;
    private double lastX = 0;
    private double lastY = 0;
    private boolean[] mouseButtonPressed = new boolean[9];
    private boolean isDragging;

    private Vector2f gameViewPortPos = new Vector2f();
    private Vector2f gameViewPortSize = new Vector2f();

    private MouseListener() {
    }

    public static MouseListener get() {
        if (instance == null) instance = new MouseListener();
        return instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button < 0 || button >= get().mouseButtonPressed.length) return;

        if (action == GLFW_PRESS) {
            get().mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getOrthoX() {
        float currentX = getX() - get().gameViewPortPos.x;
        currentX = (currentX / get().gameViewPortSize.x) * 2.0f - 1.0f;
        Vector4f mat = new Vector4f(currentX, 0, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        mat.mul(viewProjection);

        return mat.x;
    }

    public static float getOrthoY() {
        float currentY = getY() - get().gameViewPortPos.y;
        currentY = -((currentY / get().gameViewPortSize.y) * 2.0f - 1.0f);
        Vector4f mat = new Vector4f(0, currentY, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection);
        mat.mul(viewProjection);

        return mat.y;
    }

    public static float getDx() {
        return (float) (get().xPos - get().lastX);
    }

    public static float getDy() {
        return (float) (get().yPos - get().lastY);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public Vector2f getGameViewPortPos() {
        return gameViewPortPos;
    }

    public static void setGameViewPortPos(Vector2f gameViewPortPos) {
        get().gameViewPortPos = gameViewPortPos;
    }

    public Vector2f getGameViewPortSize() {
        return gameViewPortSize;
    }

    public static void setGameViewPortSize(Vector2f gameViewPortSize) {
        get().gameViewPortSize = gameViewPortSize;
    }

    public static boolean isMouseButtonDown(int button) {
        if (button < 0 || button >= get().mouseButtonPressed.length) return false;
        return get().mouseButtonPressed[button];
    }
}

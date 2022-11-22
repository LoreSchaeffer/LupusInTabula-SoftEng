package it.multicoredev.client.ui;

import it.multicoredev.client.LupusInTabula;
import it.multicoredev.client.exceptions.GraphicException;
import it.multicoredev.client.ui.listeners.KeyListener;
import it.multicoredev.client.ui.listeners.MouseListener;
import it.multicoredev.client.ui.registries.Scenes;
import it.multicoredev.client.ui.utils.ImageLoader;
import it.multicoredev.utils.LitLogger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance;

    private int width;
    private int height;
    private String title;
    private boolean vsync = true;
    private long windowId;
    private float[] windowColor = new float[]{0.1f, 0.1f, 0.1f, 1f};
    private Scene currentScene;

    private Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public static Window create(int width, int height, String title) {
        if (instance != null) throw new RuntimeException("Window already initialized");
        instance = new Window(width, height, title);
        return instance;
    }

    public static Window get() {
        if (instance == null) throw new RuntimeException("Window not initialized");
        return instance;
    }

    public static int getWidth() {
        return get().width;
    }

    public static void setWidth(int width) {
        get().width = width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setHeight(int height) {
        get().height = height;
    }

    public static long getId() {
        return get().windowId;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public static void setScene(Scenes scene) {
        try {
            Scene newScene = scene.getInstance();
            get().currentScene = newScene;
            newScene.init();
            newScene.start();
        } catch (GraphicException e) {
            LitLogger.get().error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    public static void enableVsync() {
        get().vsync = true;
        glfwSwapInterval(1);
    }

    public static void disableVsync() {
        get().vsync = false;
        glfwSwapInterval(0);
    }

    public void run() throws GraphicException {
        init();
        loop();
        destroy();
    }

    private void init() throws GraphicException {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) throw new GraphicException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        windowId = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowId == NULL) throw new GraphicException("Failed to create Game window");

        glfwSetCursorPosCallback(windowId, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(windowId, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(windowId, MouseListener::mouseScrollCallback);

        glfwSetKeyCallback(windowId, KeyListener::keyCallback);

        glfwSetWindowSizeCallback(windowId, (w, newWidth, newHeight) -> {
            width = newWidth;
            height = newHeight;
        });

        glfwSetWindowCloseCallback(windowId, this::stop);

        glfwMakeContextCurrent(windowId);
        if (vsync) glfwSwapInterval(1);
        else glfwSwapInterval(0);

        try {
            ImageLoader.setWindowIcon("assets/icon.png");
        } catch (IOException e) {
            LitLogger.get().error(e.getMessage(), e);
        }

        glfwShowWindow(windowId);
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        //TODO Load resources

        glViewport(0, 0, 2560, 1440); //TODO Replace with screen size

        setScene(Scenes.BOOTSTRAP);
    }

    private void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float deltaTime = -1.0f;

        while (!glfwWindowShouldClose(windowId)) {
            glfwPollEvents();

            glClearColor(windowColor[0], windowColor[1], windowColor[2], windowColor[3]);
            glClear(GL_COLOR_BUFFER_BIT);

            if (deltaTime >= 0) {
                currentScene.update(deltaTime);
            }

            glfwSwapBuffers(windowId);

            endTime = (float) glfwGetTime();

            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void destroy() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void stop(long id) {
        LupusInTabula.get().stop();
    }
}

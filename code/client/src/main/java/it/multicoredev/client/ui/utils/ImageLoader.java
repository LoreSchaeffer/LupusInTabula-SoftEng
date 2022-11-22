package it.multicoredev.client.ui.utils;

import it.multicoredev.client.ui.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class ImageLoader {

    public static void setWindowIcon(String path) throws IOException {
        try (InputStream is = ImageLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new NullPointerException("Image not found");

            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer channels = BufferUtils.createIntBuffer(1);

            byte[] bytes = is.readAllBytes();
            ByteBuffer imgByteBuf = BufferUtils.createByteBuffer(bytes.length);
            imgByteBuf.put(bytes);
            imgByteBuf.flip();

            ByteBuffer imgBuf = stbi_load_from_memory(imgByteBuf, width, height, channels, 0);

            if (imgBuf == null) throw new IOException("Failed to load image: " + stbi_failure_reason());

            GLFWImage image = GLFWImage.malloc();
            image.set(width.get(0), height.get(0), imgBuf);
            GLFWImage.Buffer images = GLFWImage.malloc(1);
            images.put(0, image);

            glfwSetWindowIcon(Window.getId(), images);

            images.free();
            image.free();
        }
    }
}

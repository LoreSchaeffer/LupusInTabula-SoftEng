package it.multicoredev.client.ui;

import it.multicoredev.utils.LitLogger;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class IconLoader {

    public static Image loadIcon(String path) {
        try {
            return new ImageIcon(Objects.requireNonNull(IconLoader.class.getClassLoader().getResource(path))).getImage();
        } catch (Exception e) {
            LitLogger.error("Failed to load icon: " + path, e);
            return null;
        }
    }
}

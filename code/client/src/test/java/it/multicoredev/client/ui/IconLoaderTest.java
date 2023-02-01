package it.multicoredev.client.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IconLoaderTest {

    @Test
    void loadIcon() {
        assertNotNull(IconLoader.loadIcon("assets/icon.png"));
    }
}
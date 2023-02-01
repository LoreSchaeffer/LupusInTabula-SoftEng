package it.multicoredev.client.assets;

import it.multicoredev.text.Text;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LocaleTest {

    @Test
    @SuppressWarnings("unchecked")
    void init() throws Exception {
        Locale locale = new Locale();
        locale.init();

        Field textsField = Locale.class.getDeclaredField("texts");
        textsField.setAccessible(true);
        Map<String, String> texts = (HashMap<String, String>) textsField.get(locale);

        assertNotNull(texts);

        for (Text text : Text.values()) {
            assertTrue(texts.containsKey(text.getPath()));
        }
    }
}
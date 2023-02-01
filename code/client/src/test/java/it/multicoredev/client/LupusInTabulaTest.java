package it.multicoredev.client;

import it.multicoredev.client.assets.Locale;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LupusInTabulaTest {

    @Test
    void createMain() {
        assertNotNull(LupusInTabula.get());
    }

    @Test
    void initConfig() throws Exception {
        LupusInTabula lit = LupusInTabula.get();

        Method initConfigsMethod = LupusInTabula.class.getDeclaredMethod("initConfigs");
        initConfigsMethod.setAccessible(true);
        initConfigsMethod.invoke(lit);

        Field configField = LupusInTabula.class.getDeclaredField("config");
        configField.setAccessible(true);
        assertNotNull(configField.get(lit));
    }

    @Test
    @SuppressWarnings("unchecked")
    void initLocalizations() throws Exception {
        LupusInTabula lit = LupusInTabula.get();

        Method initConfigsMethod = LupusInTabula.class.getDeclaredMethod("initConfigs");
        initConfigsMethod.setAccessible(true);
        initConfigsMethod.invoke(lit);

        Field localizationsField = LupusInTabula.class.getDeclaredField("localizations");
        localizationsField.setAccessible(true);

        assertNotNull(localizationsField.get(lit));
        assertNotEquals(0, ((HashMap<String, Locale>) localizationsField.get(lit)).size());
        assertTrue(((HashMap<String, Locale>) localizationsField.get(lit)).containsKey("en_us"));
    }
}

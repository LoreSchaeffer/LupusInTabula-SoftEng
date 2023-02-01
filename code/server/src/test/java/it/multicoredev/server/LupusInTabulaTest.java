package it.multicoredev.server;

import it.multicoredev.server.models.ServerPlayer;
import it.multicoredev.utils.Encryption;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

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
    void secretCreation() {
        LupusInTabula lit = LupusInTabula.get();
        lit.start();

        assertNotNull(Encryption.getSecret());
    }

    @Test
    void clientIdGeneration() {
        LupusInTabula lit = LupusInTabula.get();

        assertNotNull(lit.getNewClientId());
    }

    @Test
    void codeGeneration() throws Exception {
        LupusInTabula lit = LupusInTabula.get();

        Method createCodeMethod = LupusInTabula.class.getDeclaredMethod("createCode");
        createCodeMethod.setAccessible(true);

        assertNotNull(createCodeMethod.invoke(lit));
    }

    @Test
    void createGame() {
        LupusInTabula lit = LupusInTabula.get();

        assertNotNull(lit.createGame(new ServerPlayer(UUID.randomUUID(), "test", true, null)));
    }
  
}
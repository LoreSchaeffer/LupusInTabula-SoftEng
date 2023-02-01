package it.multicoredev.server.models;

import com.google.gson.Gson;
import it.multicoredev.server.LupusInTabula;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServerGameTest {

    @Test
    void serialize() {
        Gson gson = new Gson();
        assertEquals(
                "{\"code\":\"ABCDEF\",\"players\":[{\"master\":true,\"alive\":true,\"message_channel\":\"ALL\",\"role_known_by\":[],\"uuid\":\"fd767183-9f48-4550-bdd5-9da7e574bd5b\",\"name\":\"test\"}],\"state\":0,\"day\":1,\"night\":false}",
                gson.toJson(new ServerGame(
                        "ABCDEF",
                        new ServerPlayer(UUID.fromString("fd767183-9f48-4550-bdd5-9da7e574bd5b"),"test", true, null),
                        LupusInTabula.get()
                )));
    }

}
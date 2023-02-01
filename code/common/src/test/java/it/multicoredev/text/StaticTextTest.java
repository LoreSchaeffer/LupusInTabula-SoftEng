package it.multicoredev.text;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaticTextTest {

    @Test
    void serialize() {
        Gson gson = new Gson();
        assertEquals("{\"text\":\"test\",\"type\":\"static\"}", gson.toJson(new StaticText("test")));
    }

    @Test
    void deserialize() {
        Gson gson = new Gson();
        assertEquals(new StaticText("test"), gson.fromJson("{\"text\":\"test\",\"type\":\"static\"}", StaticText.class));
    }
}
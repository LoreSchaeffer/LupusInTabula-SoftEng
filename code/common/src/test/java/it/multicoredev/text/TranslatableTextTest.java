package it.multicoredev.text;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TranslatableTextTest {

    @Test
    void serialize() {
        Gson gson = new Gson();
        assertEquals("{\"path\":\"test.path\",\"args\":[\"test\",1],\"type\":\"translatable\"}", gson.toJson(new TranslatableText("test.path", "test", 1)));
    }

    @Test
    void deserialize() {
        Gson gson = new Gson();
        assertEquals(new TranslatableText("test.path", "test", 1), gson.fromJson("{\"path\":\"test.path\",\"args\":[\"test\",1],\"type\":\"translatable\"}", TranslatableText.class));
    }
}
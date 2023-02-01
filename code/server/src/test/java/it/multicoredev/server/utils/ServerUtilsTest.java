package it.multicoredev.server.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerUtilsTest {

    @Test
    void counter() {
        Map<String, Integer> counter = ServerUtils.counter(Arrays.asList("a", "b", "c", "a", "d", "f", "a", "b", "f", "g", "c", "b", "a", "h"));

        assertEquals(4, counter.get("a"));
        assertEquals(3, counter.get("b"));
        assertEquals(2, counter.get("c"));
        assertEquals(1, counter.get("d"));
        assertEquals(2, counter.get("f"));
        assertEquals(1, counter.get("g"));
        assertEquals(1, counter.get("h"));
    }

    @Test
    void getMostFrequent() {
        assertEquals("a", ServerUtils.getMostFrequent(ServerUtils.counter(Arrays.asList("a", "b", "c", "a", "d", "f", "a", "b", "f", "g", "c", "b", "a", "h"))));
    }
}
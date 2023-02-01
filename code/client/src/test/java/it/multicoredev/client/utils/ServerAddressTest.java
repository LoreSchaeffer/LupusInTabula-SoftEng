package it.multicoredev.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerAddressTest {

    @Test
    void fromStringWithoutPort() {
        assertEquals(new ServerAddress("localhost", 12987), ServerAddress.fromString("localhost"));
    }

    @Test
    void fromStringWithPort() {
        assertEquals(new ServerAddress("localhost", 12345), ServerAddress.fromString("localhost:12345"));
    }

    @Test
    void fromStringNumericWithoutPort() {
        assertEquals(new ServerAddress("192.168.0.1", 12987), ServerAddress.fromString("192.168.0.1"));
    }

    @Test
    void fromStringNumericWithPort() {
        assertEquals(new ServerAddress("192.168.0.1", 12345), ServerAddress.fromString("192.168.0.1:12345"));
    }
}
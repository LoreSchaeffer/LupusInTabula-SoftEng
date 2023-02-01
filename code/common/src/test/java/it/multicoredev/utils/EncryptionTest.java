package it.multicoredev.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionTest {

    @Test
    void encrypt() throws Exception {
        Encryption.setSecret("secret");

        assertEquals("XemVflKv0nskFTaHBQIyaA==", Encryption.encrypt("test"));
    }

    @Test
    void decrypt() throws Exception {
        assertEquals("test", Encryption.decrypt("XemVflKv0nskFTaHBQIyaA=="));
    }
}
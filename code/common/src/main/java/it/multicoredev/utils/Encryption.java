package it.multicoredev.utils;

import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;

public class Encryption {
    private static final byte[] salt = {(byte) 0x44, (byte) 0x65, (byte) 0x32, (byte) 0xc4, (byte) 0x7a, (byte) 0x3f, (byte) 0x9c, (byte) 0x12};
    private static String secret;

    public static void setSecret(@NotNull String secret) {
        if (Encryption.secret != null) throw new IllegalStateException("Secret already set");
        if (secret == null || secret.trim().isEmpty()) throw new IllegalArgumentException("Secret cannot be null or empty");
        Encryption.secret = secret;
    }

    public static String getSecret() {
        return secret;
    }

    public static String encrypt(@NotNull String input) throws GeneralSecurityException, IOException {
        byte[] decData;
        byte[] encData;
        Cipher cipher = makeCipher(true, secret);
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        int blockSize = 8;
        int paddedCount = blockSize - (input.getBytes().length % blockSize);
        int padded = input.getBytes().length + paddedCount;

        decData = new byte[padded];
        is.read(decData);
        is.close();

        for (int i = input.getBytes().length; i < padded; ++i) {
            decData[i] = (byte) paddedCount;
        }

        encData = cipher.doFinal(decData);

        return Base64.getEncoder().encodeToString(encData);
    }

    public static String decrypt(@NotNull String input) throws GeneralSecurityException {
        byte[] encData;
        byte[] decData;
        Cipher cipher = makeCipher(false, secret);

        encData = Base64.getDecoder().decode(input.getBytes());

        decData = cipher.doFinal(encData);

        int padCount = (int) decData[decData.length - 1];
        if (padCount >= 1 && padCount <= 8) {
            decData = Arrays.copyOfRange(decData, 0, decData.length - padCount);
        }

        return new String(decData, StandardCharsets.UTF_8);
    }

    private static Cipher makeCipher(Boolean encryptMode, String password) throws GeneralSecurityException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        if (encryptMode) cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        else cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);

        return cipher;
    }
}

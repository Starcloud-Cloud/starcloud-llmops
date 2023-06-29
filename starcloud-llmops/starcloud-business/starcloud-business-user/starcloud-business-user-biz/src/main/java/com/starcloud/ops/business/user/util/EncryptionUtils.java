package com.starcloud.ops.business.user.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtils {

    private static final String KEY = "1234567890abcdef";


    private static final Long ENCRYPTION_KEY = 123456L;

    public static String encryptString(String originalString) throws Exception {
        byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = cipher.doFinal(originalString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static Long encrypt(Long number) {
        return (number + ENCRYPTION_KEY) % 1000000;
    }

    public static Long decrypt(String encryptedNumber) {
        Long aLong = Long.valueOf(encryptedNumber);
        return (aLong - ENCRYPTION_KEY + 1000000) % 1000000;
    }

    public static String decryptString(String encryptedString) throws Exception {
        byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}

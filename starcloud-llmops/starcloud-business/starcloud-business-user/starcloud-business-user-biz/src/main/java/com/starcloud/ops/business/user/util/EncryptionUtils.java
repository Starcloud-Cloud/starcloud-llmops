package com.starcloud.ops.business.user.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtils {

    private static final String KEY = "1234567890abcdef";


    private static final long ENCRYPTION_KEY = 12345L;

    public static String encryptString(String originalString) throws Exception {
        byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedBytes = cipher.doFinal(originalString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String encrypt(Long number) {
        Long encryptedNumber = number + ENCRYPTION_KEY;
        if (encryptedNumber < 1000000) {
            return String.format("%06d", encryptedNumber);
        }
        return String.valueOf(encryptedNumber);
    }

    public static Long decrypt(String code) {
        int encryptedNumber = Integer.parseInt(code);
        return encryptedNumber - ENCRYPTION_KEY;
    }

    public static Long decrypt(Long aLong) {
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

    public static String calculateMD5UID(String input) {
        // 创建MD5哈希算法的实例
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 将字符串转换为字节数组
        byte[] byteArray = input.getBytes(StandardCharsets.UTF_8);
        // 计算字节数组的哈希值
        byte[] mdBytes = md.digest(byteArray);

        // 将哈希值转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte mdByte : mdBytes) {
            sb.append(Integer.toHexString(mdByte & 0xFF));
        }
        return sb.toString();
    }

}

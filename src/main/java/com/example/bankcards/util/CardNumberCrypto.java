package com.example.bankcards.util;

import com.example.bankcards.constant.BankRestErrorCode;
import com.example.bankcards.exception.BankRestRuntimeException;
import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Шифрование/дешифрование номера карты.
 */
@UtilityClass
public class CardNumberCrypto {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String mask(String number) {
        String last4 = number.substring(number.length() - 4);
        return "**** **** **** " + last4;
    }

    public static String numberToMasked(String number, String key) {
        if (number == null) {
            return null;
        }
        return mask(decrypt(number, key));
    }

    public static String encrypt(String value, String key) {
        try {
            SecretKeySpec aesKey = keyFromString(key);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось зашифровать номер карты", e);
        }
    }

    public static String decrypt(String value, String key) {
        try {
            SecretKeySpec aesKey = keyFromString(key);
            byte[] combined = Base64.getDecoder().decode(value);
            if (combined.length <= IV_LENGTH_BYTES) {
                throw new BankRestRuntimeException(BankRestErrorCode.BAD_REQUEST, "Некорректный формат зашифрованного номера");
            }
            byte[] iv = Arrays.copyOfRange(combined, 0, IV_LENGTH_BYTES);
            byte[] ciphertext = Arrays.copyOfRange(combined, IV_LENGTH_BYTES, combined.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (BankRestRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось расшифровать номер карты", e);
        }
    }

    private static SecretKeySpec keyFromString(String key) {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(key.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать ключ шифрования", e);
        }
    }
}

package com.example.fraga.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays; // Untuk perbandingan byte array

public class PasswordUtils {

    private static final String HASHING_ALGORITHM = "SHA-256"; // Atau "SHA-512"
    private static final int SALT_LENGTH_BYTES = 16; // Panjang salt dalam byte

    // Menghasilkan salt acak
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);
        return salt;
    }

    // Menghash kata sandi dengan salt yang diberikan
    public static byte[] hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
            // Tambahkan salt ke digest sebelum kata sandi (atau setelah, konsisten saja)
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            // Untuk keamanan lebih, Anda bisa menggabungkan salt dan hash sebelum menyimpannya
            // atau menyimpan salt secara terpisah dari hash.
            // Di sini kita akan mengembalikan hanya hash, salt akan disimpan terpisah.
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            // Algoritma tidak ditemukan, seharusnya tidak terjadi untuk SHA-256
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    // Memverifikasi kata sandi yang dimasukkan dengan hash dan salt yang disimpan
    public static boolean verifyPassword(String providedPassword, byte[] storedHash, byte[] salt) {
        byte[] hashedProvidedPassword = hashPassword(providedPassword, salt);
        return Arrays.equals(hashedProvidedPassword, storedHash);
    }

    // Metode helper untuk konversi byte array ke String hex (untuk penyimpanan di DB jika tidak mau blob)
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Metode helper untuk konversi String hex ke byte array
    public static byte[] hexStringToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
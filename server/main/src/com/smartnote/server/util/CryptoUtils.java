package com.smartnote.server.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for cryptography.
 * 
 * @author Ethan Vrhel
 */
public class CryptoUtils {

    private static SecureRandom RANDOM = null;

    private static final String CHAR_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Initializes the utility class.
     * 
     * @param algorithm The algorithm to use for random number generation. If
     *                 <code>null</code>, the default algorithm is used.
     * @throws NoSuchAlgorithmException If the algorithm is not supported.
     */
    public static void init(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            RANDOM = new SecureRandom();
        } else {
            RANDOM = SecureRandom.getInstance(algorithm);
        }
    }

    /**
     * Gets the random number generator.
     * 
     * @return The random number generator.
     */
    public static SecureRandom getRandom() {
        return RANDOM;
    }

    /**
     * Generates random bytes.
     * 
     * @param bytes The byte array to fill.
     */
    public static void randomBytes(byte[] bytes) {
        RANDOM.nextBytes(bytes);
    }

    /**
     * Generates a random string.
     * 
     * @param length The length of the string.
     * @return The random string.
     */
    public static String randomString(int length) {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++)
            chars[i] = CHAR_ARRAY.charAt(RANDOM.nextInt(CHAR_ARRAY.length()));
        return new String(chars);
    }
    
    // don't allow instantiation
    private CryptoUtils() {}
}

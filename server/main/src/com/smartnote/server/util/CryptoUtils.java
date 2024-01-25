package com.smartnote.server.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * <p>Utility class for cryptography. Provides methods that generate
 * cryptographically secure random data.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
public class CryptoUtils {

    // secure random number generator for the server
    private static SecureRandom RANDOM = null;

    // character array for random string generation
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

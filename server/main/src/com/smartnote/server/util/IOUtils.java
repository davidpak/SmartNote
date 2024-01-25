package com.smartnote.server.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * IO utilities.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.util.FileUtils
 */
public class IOUtils {

    /**
     * Read all remaining bytes from an input stream.
     * 
     * @param in The input stream.
     * @return The bytes read.
     * @throws IOException If an I/O error occurs.
     */
    public static byte[] readAllBytes(InputStream in) throws IOException {
        byte[] result = new byte[0];
        byte[] buffer = new byte[1024];

        int read = 0;
        while ((read = in.read(buffer)) != -1) {
            byte[] temp = new byte[result.length + read];
            System.arraycopy(result, 0, temp, 0, result.length);
            System.arraycopy(buffer, 0, temp, result.length, read);
            result = temp;
        }

        return result;
    }
}

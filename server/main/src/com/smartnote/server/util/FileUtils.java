package com.smartnote.server.util;

import java.io.File;
import java.nio.file.Files;

/**
 * File utilities.
 * 
 * @author Ethan Vrhel
 */
public class FileUtils {
    
    /**
     * Deletes a file or directory. If the file is a directory, all files
     * in the directory will be deleted recursively.
     * 
     * @param f The file to delete.
     */
    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            for (File f2 : f.listFiles())
                deleteFile(f2);
        }

        f.delete();
    }

    /**
     * Deletes a file or directory. If the file is a directory, all files
     * in the directory will be deleted recursively.
     * 
     * @param path The path to the file to delete.
     */
    public static void deleteFile(String path) {
        deleteFile(new File(path));
    }
    
    /**
     * Reads a file.
     * 
     * @param f The file to read.
     * @return The contents of the file.
     * 
     * @throws Exception If an error occurs.
     */
    public static String readFile(File f) throws Exception {
        return new String(Files.readAllBytes(f.toPath()));
    }

    /**
     * Reads a file.
     * 
     * @param path The path to the file to read.
     * @return The contents of the file.
     * 
     * @throws Exception If an error occurs.
     */
    public static String readFile(String path) throws Exception {
        return readFile(new File(path));
    }

    /**
     * Tests if a file is in a directory or any of its subdirectories.s
     * 
     * @param file The file.
     * @param directory The directory.
     * @return <code>true</code> if the file is in the directory or any of
     *        its subdirectories, <code>false</code> otherwise.
     */
    public static boolean isFileInDirectory(File file, File directory) {
        String filePath = getCanonicalPath(file);
        String directoryPath = getCanonicalPath(directory);
        return filePath.startsWith(directoryPath);
    }

    public static File getCanonicalFile(File f) {
        try {
            return f.getCanonicalFile();
        } catch (Exception e) {
            return f.getAbsoluteFile();
        }
    }

    public static String getCanonicalPath(File f) {
        try {
            return f.getCanonicalPath();
        } catch (Exception e) {
            return f.getAbsolutePath();
        }
    }

    public static File path(File... files) {
        File f = files[0];
        for (int i = 1; i < files.length; i++)
            f = new File(f, files[i].getPath());
        return f;
    }

    public static String path(String... files) {
        String f = files[0];
        for (int i = 1; i < files.length; i++)
            f = new File(f, files[i]).getPath();
        return f;
    }

    // don't allow instantiation
    private FileUtils() {}
}

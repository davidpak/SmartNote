package com.smartnote.server.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Objects;

/**
 * File utilities.
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Resource
 */
public class FileUtils {
    
    /**
     * Deletes a file or directory. If the file is a directory, all files
     * in the directory will be deleted recursively.
     * 
     * @param f The file to delete. If <code>null</code>, nothing happens.
     * @throws SecurityException If a security manager exists and its
     *      <code>checkDelete</code> method denies delete access to the file or
     *      <code>checkRead</code> method denies read access to the file.
     */
    public static void deleteFile(File f) throws SecurityException {
        if (f == null) return;

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
     * @throws SecurityException If a security manager exists and its
     *     <code>checkDelete</code> method denies delete access to the file or
     *     <code>checkRead</code> method denies read access to the file.
     */
    public static void deleteFile(String path) throws SecurityException {
        if (path == null) return;
        deleteFile(new File(path));
    }
    
    /**
     * Reads a file.
     * 
     * @param f The file to read.
     * @return The contents of the file. Never <code>null</code>.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException If an I/O error occurs.
     * @throws OutOfMemoryError If there is not enough memory to read the file.
     * @throws SecurityException If a security manager exists and its
     *       <code>checkRead</code> method denies read access to the file.
     */
    public static String readFile(File f) throws InvalidPathException,
        IOException, OutOfMemoryError, SecurityException {
        return new String(Files.readAllBytes(f.toPath()));
    }

    /**
     * Reads a file.
     * 
     * @param path The path to the file to read.
     * @return The contents of the file. Never <code>null</code>.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException If an I/O error occurs.
     * @throws OutOfMemoryError If there is not enough memory to read the file.
     * @throws SecurityException If a security manager exists and its
     *       <code>checkRead</code> method denies read access to the file.
     */
    public static String readFile(String path) throws InvalidPathException,
        IOException, OutOfMemoryError, SecurityException {
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

    /**
     * Gets the canonical file of a file. If the canonical file cannot be
     * retrieved, the absolute file is returned.
     * 
     * @param f The file.
     * @return The canonical file.
     * @throws SecurityException If a security manager exists and a system
     * property cannot be accessed.
     */
    public static File getCanonicalFile(File f) throws SecurityException {
        try {
            return f.getCanonicalFile();
        } catch (Exception e) {
            return f.getAbsoluteFile();
        }
    }

    /**
     * Gets the canonical path of a file. If the canonical path cannot be
     * retrieved, the absolute path is returned.
     * 
     * @param f The file.
     * @return The canonical path.
     * @throws SecurityException If a security manager exists and a system
     * property cannot be accessed.
     */
    public static String getCanonicalPath(File f) throws SecurityException {
        try {
            return f.getCanonicalPath();
        } catch (Exception e) {
            return f.getAbsolutePath();
        }
    }

    /**
     * Creates a file path from a list of files.
     * 
     * @param files The files.
     * @return The file path.
     */
    public static File path(File... files) {
        File f = files[0];
        for (int i = 1; i < files.length; i++)
            f = new File(f, files[i].getPath());
        return f;
    }

    /**
     * Creates a file path from a list of files.
     * 
     * @param files The files.
     * @return The file path.
     */
    public static String path(String... files) {
        String f = files[0];
        for (int i = 1; i < files.length; i++)
            f = new File(f, files[i]).getPath();
        return f;
    }

    /**
     * Calculates the size of a directory by adding the size of all
     * files in the directory and its subdirectories.
     * 
     * @param directory The directory.
     * @return The size of the directory.
     * @throws NullPointerException If the directory is <code>null</code>.
     * @throws SecurityException If a security manager exists and its
     *    <code>checkRead</code> method denies read access to the file.
     */
    public static long getDirectorySize(File directory) throws NullPointerException, SecurityException {
        Objects.requireNonNull(directory);

        long size = 0;
        for (File f : directory.listFiles()) {
            if (f.isDirectory())
                size += getDirectorySize(f);
            else
                size += f.length();
        }
        return size;
    }

    // don't allow instantiation
    private FileUtils() {}
}

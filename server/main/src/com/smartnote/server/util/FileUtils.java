package com.smartnote.server.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
     *                           <code>checkDelete</code> method denies delete
     *                           access to the file or
     *                           <code>checkRead</code> method denies read access to
     *                           the file.
     */
    public static void deleteFile(File f) throws SecurityException {
        if (f == null)
            return;

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
     *                           <code>checkDelete</code> method denies delete
     *                           access to the file or
     *                           <code>checkRead</code> method denies read access to
     *                           the file.
     */
    public static void deleteFile(String path) throws SecurityException {
        if (path == null)
            return;
        deleteFile(new File(path));
    }

    /**
     * Reads a file.
     * 
     * @param file The file to read.
     * @return The contents of the file. Never <code>null</code>.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException          If an I/O error occurs.
     * @throws OutOfMemoryError     If there is not enough memory to read the file.
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkRead</code> method denies read access
     *                              to the file.
     */
    public static String readFile(File file) throws InvalidPathException,
            IOException, OutOfMemoryError, SecurityException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    /**
     * Reads a file.
     * 
     * @param path The path to the file to read.
     * @return The contents of the file. Never <code>null</code>.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException          If an I/O error occurs.
     * @throws OutOfMemoryError     If there is not enough memory to read the file.
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkRead</code> method denies read access
     *                              to the file.
     */
    public static String readFile(String path) throws InvalidPathException,
            IOException, OutOfMemoryError, SecurityException {
        return readFile(new File(path));
    }

    /**
     * Writes data to a file.
     * 
     * @param file The file to write to.
     * @param data The data to write.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException          If an I/O error occurs.
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkWrite</code> method denies write
     *                              access to the file.
     */
    public static void writeFile(File file, byte[] data) throws InvalidPathException,
            IOException, SecurityException {
        Files.write(file.toPath(), data, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * Writes data to a file.
     * 
     * @param file The file to write to.
     * @param data The data to write.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException          If an I/O error occurs.
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkWrite</code> method denies write
     *                              access to the file.
     */
    public static void writeFile(File file, String data) throws InvalidPathException,
            IOException, SecurityException {
        writeFile(file, data.getBytes());
    }

    /**
     * Writes data to a file.
     * 
     * @param path The path to the file to write to.
     * @param data The data to write.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException          If an I/O error occurs.
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkWrite</code> method denies write
     *                              access to the file.
     */
    public static void writeFile(String path, byte[] data) throws InvalidPathException,
            IOException, SecurityException {
        writeFile(new File(path), data);
    }

    /**
     * Writes data to a file.
     * 
     * @param path The path to the file to write to.
     * @param data The data to write.
     * 
     * @throws InvalidPathException If the path is invalid.
     * @throws IOException          If an I/O error occurs.
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkWrite</code> method denies write
     *                              access to the file.
     */
    public static void writeFile(String path, String data) throws InvalidPathException,
            IOException, SecurityException {
        writeFile(new File(path), data.getBytes());
    }

    /**
     * Tests if a file is in a directory or any of its subdirectories.s
     * 
     * @param file      The file.
     * @param directory The directory.
     * @return <code>true</code> if the file is in the directory or any of
     *         its subdirectories, <code>false</code> otherwise.
     */
    public static boolean isFileInDirectory(File file, File directory) {
        String filePath = getCanonicalPath(file);
        String directoryPath = getCanonicalPath(directory);
        return filePath.startsWith(directoryPath);
    }

    public static boolean isPathInDirectory(Path path, Path directory) {
        String filePath = getCanonicalPath(path.toFile());
        String directoryPath = getCanonicalPath(directory.toFile());
        return filePath.startsWith(directoryPath);
    }

    /**
     * Gets the canonical file of a file. If the canonical file cannot be
     * retrieved, the absolute file is returned.
     * 
     * @param f The file.
     * @return The canonical file.
     * @throws SecurityException If a security manager exists and a system
     *                           property cannot be accessed.
     */
    public static File getCanonicalFile(File f) throws SecurityException {
        try {
            return f.getCanonicalFile();
        } catch (Exception e) {
            return f.getAbsoluteFile();
        }
    }

    /**
     * Gets the canonical file of a file. If the canonical file cannot be
     * retrieved, the absolute file is returned.
     * 
     * @param path The path to the file.
     * @return The canonical file.
     * @throws SecurityException If a security manager exists and a system
     *                           property cannot be accessed.
     */
    public static File getCanonicalFile(String path) throws SecurityException {
        return getCanonicalFile(new File(path));
    }

    /**
     * Gets the canonical path of a file. If the canonical path cannot be
     * retrieved, the absolute path is returned.
     * 
     * @param f The file.
     * @return The canonical path.
     * @throws SecurityException If a security manager exists and a system
     *                           property cannot be accessed.
     */
    public static String getCanonicalPath(File f) throws SecurityException {
        try {
            return f.getCanonicalPath();
        } catch (Exception e) {
            return f.getAbsolutePath();
        }
    }

    /**
     * Gets the canonical path of a file. If the canonical path cannot be
     * retrieved, the absolute path is returned.
     * 
     * @param path The path to the file.
     * @return The canonical path.
     * @throws SecurityException If a security manager exists and a system
     *                           property cannot be accessed.
     */
    public static String getCanonicalPath(String path) throws SecurityException {
        return getCanonicalPath(new File(path));
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
     * @throws SecurityException    If a security manager exists and its
     *                              <code>checkRead</code> method denies read access
     *                              to the file.
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

    public static String getExtension(Path path) {
        Objects.requireNonNull(path);

        String name = path.getFileName().toString();
        int index = name.lastIndexOf('.');
        if (index == -1)
            return "";
        return name.substring(index + 1);
    }

    /**
     * Gets the extension of a file.
     * 
     * @param file The file. Cannot be <code>null</code>.
     * @return The extension, or an empty string if the file has no extension.
     */
    public static String getExtension(File file) {
        return getExtension(Objects.requireNonNull(file).toPath());
    }

    /**
     * Gets the extension of a file.
     * 
     * @param path The path to the file. Cannot be <code>null</code>.
     * @return The extension, or an empty string if the file has no extension.
     */
    public static String getExtension(String path) {
        return getExtension(Paths.get(Objects.requireNonNull(path)));
    }    

    /**
     * Removes the extension from a file.
     * 
     * @param path The path to the file. Cannot be <code>null</code>.
     * @return The path without the extension.
     */
    public static Path removeExtension(Path path) {
        Objects.requireNonNull(path);
        Path parent = path.getParent();
        String name = path.getFileName().toString();
        
        int index = name.lastIndexOf('.');
        if (index == -1)
            return path;
        return parent.resolve(name.substring(0, index));
    }

    /**
     * Removes the extension from a file.
     * 
     * @param file The file. Cannot be <code>null</code>.
     * @return The file without the extension.
     */
    public static File removeExtension(File file) {
        Objects.requireNonNull(file);
        return removeExtension(file.toPath()).toFile();
    }

    /**
     * Removes the extension from a file.
     * 
     * @param path The path to the file. Cannot be <code>null</code>.
     * @return The path without the extension.
     */
    public static String removeExtension(String path) {
        Objects.requireNonNull(path);
        return removeExtension(Paths.get(path)).toString();
    }

    // don't allow instantiation
    private FileUtils() {}
}

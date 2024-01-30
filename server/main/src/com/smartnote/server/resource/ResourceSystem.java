package com.smartnote.server.resource;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.AllPermission;
import java.security.Permission;
import java.util.Objects;

import com.smartnote.server.auth.SessionPermission;
import com.smartnote.server.util.FileUtils;

/**
 * <p>
 * The resource system controls access to resources. Resources are abstract
 * objects that can be read from and written to. They are accessed through an
 * authority and a path. There are three types of resources:
 * <code>public</code>,
 * <code>private</code>, and <code>session</code>. See the documentation for
 * <code>findResource</code> for more information.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.Resource
 */
public class ResourceSystem {

    /**
     * The public authority.
     */
    public static final String PUBLIC_AUTH = "public";

    /**
     * The private authority.
     */
    public static final String PRIVATE_AUTH = "private";

    /**
     * The session authority.
     */
    public static final String SESSION_AUTH = "session";

    /**
     * Creates a new public resource name.
     * 
     * @param path The path.
     * @return The name.
     */
    public static final String inPublic(String path) {
        return PUBLIC_AUTH + ":" + path;
    }

    /**
     * Creates a new private resource name.
     * 
     * @param path The path.
     * @return The name.
     */
    public static final String inPrivate(String path) {
        return PRIVATE_AUTH + ":" + path;
    }

    /**
     * Creates a new session resource name.
     * 
     * @param path The path.
     * @return The name.
     */
    public static final String inSession(String path) {
        return SESSION_AUTH + ":" + path;
    }

    /**
     * Gets permissions allowing access to private resources. Be careful when
     * using this.
     * 
     * @return The permissions.
     */
    public static Permission getPrivatePermission() {
        return new AllPermission();
    }

    private Path publicDir;
    private Path privateDir;
    private Path sessionDir;

    private FileResourceFactory fileResourceFactory;

    /**
     * Creates a new ResourceSystem object with the specified configuration.
     * 
     * @param config The configuration. Cannot be <code>null</code>.
     */
    public ResourceSystem(ResourceConfig config) {
        this.publicDir = FileUtils.getCanonicalFile(config.getPrivateDir()).toPath();
        this.privateDir = FileUtils.getCanonicalFile(config.getPublicDir()).toPath();
        this.sessionDir = FileUtils.getCanonicalFile(config.getSessionDir()).toPath();
        
        this.fileResourceFactory = (file, mode) -> new FileResource(file.toFile(), mode);
    }

    /**
     * Retrive absolute path to public directory.
     * 
     * @return The path.
     */
    public Path getPublicDir() {
        return publicDir;
    }

    /**
     * Retrive absolute path to private directory.
     * 
     * @return The path.
     */
    public Path getPrivateDir() {
        return privateDir;
    }

    /**
     * Retrive absolute path to session directory.
     * 
     * @return The path.
     */
    public Path getSessionDir() {
        return sessionDir;
    }

    /**
     * <p>
     * Finds a resource with the specified name and permission. There are three
     * types of resources: <code>public</code>, <code>private</code>, and
     * <code>session</code>. Public resources are accessible to anyone. Private
     * resources are only accessible to the server. Session resources are only
     * accessible to the session that created them.
     * </p>
     * 
     * <p>
     * These resources are accessed by prefixing <code>name</code> with the
     * resource type and a colon. For example, <code>public:file.txt</code> would
     * access the file <code>file.txt</code> in the public directory.
     * <code>private:file.txt</code> would access the file <code>file.txt</code> in
     * the private directory. <code>session:file.txt</code> would access the file
     * <code>file.txt</code> in the session directory, for the session specified
     * in <code>permission</code>.
     * </p>
     * 
     * @param name       The name of the resource. Should be in the format
     *                   <code>authority:path</code>. Cannot be <code>null</code>.
     * @param permission The permission to use. Cannot be <code>null</code>.
     * @return The resource. Never <code>null</code>.
     * @throws SecurityException       If the permission is not sufficient to access
     *                                 the resource.
     * @throws InvalidPathException    If the path is invalid.
     * @throws NoSuchResourceException If the resource does not exist, or is not
     *                                 accessible.
     * @throws IOException             If an I/O error occurs.
     */
    public Resource findResource(String name, Permission permission)
            throws SecurityException, InvalidPathException, NoSuchResourceException, IOException {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(permission, "permission cannot be null");

        int colonIndex = name.indexOf(':');
        if (colonIndex == -1)
            throw new NoSuchResourceException(name);

        // parse authority and path
        String authority = name.substring(0, colonIndex);
        String path = name.substring(colonIndex + 1);
        Path rest = Paths.get(path);

        // find resource
        try {
            if (authority.equals(PUBLIC_AUTH))
                return getPublicResource(rest, permission);
            else if (authority.equals(PRIVATE_AUTH))
                return getPrivateResource(rest, permission);
            else if (authority.equals(SESSION_AUTH))
                return getSessionResource(rest, permission);
        } catch (NoSuchResourceException e) {
            throw new NoSuchResourceException(name); // rethrow with original name
        }

        // unknown authority
        throw new NoSuchResourceException(name);
    }

    private Resource getPublicResource(Path path, Permission permission)
            throws SecurityException, InvalidPathException, NoSuchResourceException, IOException {
        return fileResourceFactory.openFileResource(getFullPath(publicDir, path), AccessMode.READ);
    }

    private Resource getPrivateResource(Path path, Permission permission)
            throws SecurityException, InvalidPathException, NoSuchResourceException, IOException {
        if (!permission.implies(getPrivatePermission()))
            throw new SecurityException("Access denied");
        return fileResourceFactory.openFileResource(getFullPath(privateDir, path), AccessMode.READ);
    }

    private Resource getSessionResource(Path path, Permission permission)
            throws SecurityException, InvalidPathException, NoSuchResourceException, IOException {
        if (!(permission instanceof SessionPermission))
            throw new SecurityException("Access denied");

        SessionPermission sessionPermission = (SessionPermission) permission;
        Path fullPath = sessionPermission.getSession().pathInSession(path);
        return fileResourceFactory.openFileResource(fullPath, AccessMode.READ_WRITE_DELETE);
    }

    private Path getFullPath(Path root, Path path) throws SecurityException {
        Path rest = Paths.get("");
        for (Path part : path) {
            rest = rest.resolve(part);
            if (part.toString().equals(".."))
                throw new SecurityException("Access denied");
        }

        Path fullPath = root.resolve(rest);
        if (!FileUtils.isFileInDirectory(fullPath.toFile(), root.toFile()))
            throw new SecurityException("Access denied");

        return fullPath;
    }
}
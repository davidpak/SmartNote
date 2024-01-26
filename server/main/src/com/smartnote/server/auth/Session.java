package com.smartnote.server.auth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Permission;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smartnote.server.Resource;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.util.CryptoUtils;
import com.smartnote.server.util.FileUtils;
import com.smartnote.server.util.IOUtils;

import spark.Request;
import spark.Response;

/**
 * <p>
 * Stores session information. Sessions are implemented using
 * <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">JSON Web Tokens</a>
 * (JWTs). The JWT is stored in the <code>Authorization</code> header
 * within HTTP requests and responses.
 * </p>
 * 
 * <p>
 * Sessions are only valid for a certain amount of time and do not
 * persist across server restarts.
 * </p>
 * 
 * <p>
 * Sessions have access to resources available only to the current
 * session. They may be accessed through the
 * {@link com.smartnote.server.Resource} class.
 * </p>
 * 
 * <p>
 * When this class is loaded, the following occurs:
 * </p>
 * 
 * <ol>
 * <li>A session secret is randomly generated to sign JWTs.</li>
 * <li>An executor service is started to clean up expired sessions.</li>
 * </ol>
 * 
 * <p>
 * When a session is created, the following occurs:
 * </p>
 * 
 * <ol>
 * <li>A random token is generated to identify the session.</li>
 * <li>A JWT is created and signed with the session secret.</li>
 * <li>The JWT is stored in the session directory.</li>
 * </ol>
 * 
 * <p>
 * The session directory is located in the <code>sessions</code>
 * and is named after the random token, which is stored in the
 * subject field of the JWT. In the directory, there is a file named
 * <code>.token</code> which contains the JWT.
 * </p>
 * 
 * <p>
 * Sessions should be created using the {@link #createSession()}
 * method. Sessions should be retrieved using the
 * {@link #getSession(Request)} method.
 * </p>
 * 
 * <p>
 * Sessions should be renewed only if a client made a request
 * using the session that resulted in a successful response. This
 * can be done using the {@link #updateSession()} method.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Resource
 * @see com.smartnote.server.util.CryptoUtils
 */
public class Session {
    /**
     * The issuer of the session.
     */
    public static final String ISSUER = "com.smartnote.server";

    /**
     * The length of the session in seconds.
     */
    public static final long SESSION_LENGTH = 60 * 10; // 10 minutes

    /**
     * The maximum storage quota for a session in bytes.
     */
    public static final long STORAGE_QUOTA = 1024 * 1024 * 1024; // 1 GB

    /**
     * The length of the session secret in bytes.
     */
    public static final int SECRET_LENGTH = 32;

    /**
     * The length of the session token in bytes.
     */
    public static final int TOKEN_LENGTH = 32;

    /**
     * The interval at which the session directory is cleaned.
     */
    public static final int GC_INTERVAL = 60; // 1 minute

    private static final Algorithm ALGORITHM; // algorithm for signing
    private static final JWTVerifier VERIFIER; // verifier for JWTs

    // executor service for garbage collection
    private static final ScheduledExecutorService EXECUTOR_SERVICE;

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);

    static {
        // generate cryptographically secure random string
        String secret = CryptoUtils.randomString(SECRET_LENGTH);

        // create algorithm and verifier
        ALGORITHM = Algorithm.HMAC256(secret);
        VERIFIER = JWT.require(ALGORITHM).build();

        // Garbage collection service
        //
        // This is likely inefficient, especially with a large number of
        // sessions. However, this is not a huge issue at the moment.
        EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            // This code runs every GC_INTERVAL seconds. It checks if a session
            // is valid by checking if the token exists and if the token is
            // valid. If either of these conditions are not met, the session
            // directory is deleted.

            LOG.debug("Running session garbage collector");
            forceGc();
        }, GC_INTERVAL, GC_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Forces garbage collection of sessions.
     */
    public static void forceGc() {
        File[] sessionDirs = new File(Resource.getSessionDirectory()).listFiles();
        if (sessionDirs == null)
            return;

        // Iterate over all session directories
        for (File f : sessionDirs) {
            File token = new File(f.getAbsolutePath() + File.separatorChar + ".token");

            // delete session if token does not exist
            if (!token.exists()) {
                FileUtils.deleteFile(f);
                continue;
            }

            // delete session if token is invalid
            try {
                String tokenStr = FileUtils.readFile(token);
                if (!isTokenValid(tokenStr))
                    FileUtils.deleteFile(f);
            } catch (Exception e) {
                FileUtils.deleteFile(f);
            }
        }
    }

    /**
     * Creates a session from a request.
     * 
     * @param request The request.
     * @return The session, or <code>null</code> if the session is
     *         invalid or not present.
     */
    public static Session getSession(Request request) {
        DecodedJWT jwt = null;

        String auth = request.headers("Authorization");
        try {
            jwt = VERIFIER.verify(auth);
        } catch (Exception e) {
            return null;
        }

        return new Session(jwt);
    }

    /**
     * Create a new session.
     * 
     * @return The session.
     */
    public static Session createSession() {
        String user = CryptoUtils.randomString(TOKEN_LENGTH);

        // expiration date
        Instant expr = Instant.now().plusSeconds(SESSION_LENGTH);

        // create the token
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user)
                .withExpiresAt(expr)
                .sign(ALGORITHM);

        Session session = new Session(VERIFIER.verify(token));
        session.store();
        return session;
    }

    /**
     * Checks if a token is valid.
     * 
     * @param token The token.
     * @return <code>true</code> if the token is valid, <code>false</code>.
     */
    public static boolean isTokenValid(String token) {
        try {
            VERIFIER.verify(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private DecodedJWT jwt; // JSON web token
    private File sessionDirectory; // session directory
    private File tokenFile; // file containing the token

    /**
     * Create session from a JSON web token.
     * 
     * @param jwt The JSON web token.
     */
    private Session(DecodedJWT jwt) {
        this.jwt = jwt;

        this.sessionDirectory = new File(Resource.getSessionDirectory(), jwt.getSubject());
        this.sessionDirectory = FileUtils.getCanonicalFile(sessionDirectory);

        this.tokenFile = new File(sessionDirectory, ".token");
        this.tokenFile = FileUtils.getCanonicalFile(tokenFile);
    }

    /**
     * Gets the JSON web token associated with this session.
     * 
     * @return The JSON web token.
     */
    public DecodedJWT getJWT() {
        return jwt;
    }

    public String getId() {
        return jwt.getSubject();
    }

    /**
     * Gets the session directory.
     * 
     * @return The session directory.
     */
    public File getSessionDirectory() {
        return sessionDirectory;
    }

    /**
     * Gets a file in the session directory. This includes private
     * files (as specified in {@link com.smartnote.server.Resource}).
     * The existence of these files should not be exposed to the
     * remote client.
     * 
     * 
     * @param name The name of the file.
     * @return The file, or <code>null</code> if the file does not exist
     *         or is not in the session directory.
     */
    public File getFile(String name) {
        File file = new File(sessionDirectory, name);

        if (!FileUtils.isFileInDirectory(file, sessionDirectory))
            return null;

        // hide the token file
        if (file.getPath().equalsIgnoreCase(tokenFile.getPath()))
            return null;

        return file;
    }

    /**
     * Renews the session.
     */
    public void updateSession() {
        // expiration date
        Instant expr = Instant.now().plusSeconds(SESSION_LENGTH);

        // create the token
        String token = JWT.create()
                .withIssuer(jwt.getIssuer())
                .withSubject(jwt.getSubject())
                .withExpiresAt(expr)
                .sign(ALGORITHM);

        jwt = VERIFIER.verify(token);

        store();
    }

    /**
     * Writes the session token to the response. Will be stored in the
     * <code>Authorization</code> header.
     * 
     * @param response The response.
     */
    public void writeToResponse(Response response) {
        response.header("Authorization", jwt.getToken());
    }

    /**
     * Gets the permission associated with this session.
     * 
     * @return The permission.
     */
    public Permission getPermission() {
        return new SessionPermission(this);
    }

    /**
     * Write data to a session file.
     * 
     * @param name  The name of the file.
     * @param bytes The data to write.
     * @throws IOException            If an I/O error occurs.
     * @throws IllegalAccessException The resource is not in the
     *                                session directory.
     * @throws IllegalStateException  The storage quota has been
     *                                exceeded.
     */
    public void writeSessionFile(String name, byte[] bytes)
            throws IOException, IllegalAccessException, IllegalStateException {
        if (FileUtils.getDirectorySize(sessionDirectory) + bytes.length > STORAGE_QUOTA)
            throw new IllegalStateException("Storage quota exceeded");

        OutputStream out = Resource.writeSession(name, this);
        out.write(bytes);
        out.close();
    }

    /**
     * Read data from a session resource.
     * 
     * @param name The name of the resource.
     * @return The data.
     * @throws IOException             If an I/O error occurs.
     * @throws IllegalAccessException  The resource is not in the
     *                                 session directory. The existence
     *                                 of the resource is not checked.
     * @throws NoSuchResourceException The resource does not exist.
     */
    public byte[] readSessionResource(String name) throws IOException, IllegalAccessException, NoSuchResourceException {
        InputStream in = Resource.readSession(name, this);
        byte[] result = IOUtils.readAllBytes(in);
        in.close();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Session))
            return false;

        Session s = (Session) o;
        return s.jwt.getToken().equals(jwt.getToken());
    }

    @Override
    public int hashCode() {
        return jwt.getToken().hashCode();
    }

    /**
     * Store session information in this session's directory.
     */
    private void store() {
        try {
            OutputStream out = Resource.writeSession(".token", this);
            out.write(jwt.getToken().getBytes());
            out.close();
        } catch (Exception e) {
        }
    }
}

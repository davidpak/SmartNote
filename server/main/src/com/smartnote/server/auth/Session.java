package com.smartnote.server.auth;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
import com.smartnote.server.util.CryptoUtils;
import com.smartnote.server.util.FileUtils;

import spark.Request;
import spark.Response;

/**
 * Stores session information.
 * 
 * @author Ethan Vrhel
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

    private static final ScheduledExecutorService EXECUTOR_SERVICE;

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);

    static {
        // generate cryptographically secure random string
        String secret = CryptoUtils.randomString(SECRET_LENGTH);

        // create algorithm and verifier
        ALGORITHM = Algorithm.HMAC256(secret);
        VERIFIER = JWT.require(ALGORITHM).build();

        // start garbage collector
        EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
        EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            LOG.debug("Running session garbage collector");

            File[] sessionDirs = new File(Resource.SESSION_DIR).listFiles();
            if (sessionDirs == null)
                return;

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
        }, GC_INTERVAL, GC_INTERVAL, TimeUnit.SECONDS);
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

    /**
     * Initializes the session manager.
     */
    public static void init() {
        // this method is only here to force the static initializer to run
        // when the server starts as we need to read the session secret
        // before we can do anything else
    }

    private DecodedJWT jwt;
    private File sessionDirectory;

    private Session(DecodedJWT jwt) {
        this.jwt = jwt;
        this.sessionDirectory = new File(Resource.SESSION_DIR + File.separatorChar + jwt.getSubject());
        this.sessionDirectory = FileUtils.getCanonicalFile(sessionDirectory);
    }

    /**
     * Gets the JSON web token associated with this session.
     * 
     * @return The JSON web token.
     */
    public DecodedJWT getJWT() {
        return jwt;
    }

    /**
     * Gets the session directory.
     * 
     * @return The session directory.
     */
    public File getSessionDirectory() {
        return sessionDirectory;
    }

    public File getFile(String name) {
        return new File(sessionDirectory, name);
    }

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
     * Write data to a session file.
     * 
     * @param name The name of the file.
     * @param bytes The data to write.
     * @throws IOException If an I/O error occurs.
     * @throws IllegalAccessException The resource is not in the
     * session directory.
     */
    public void writeSessionFile(String name, byte[] bytes) throws IOException, IllegalAccessException {
        OutputStream out = Resource.writeSession(name, this);
        out.write(bytes);
        out.close();
    }

    /**
     * Store session information in this session's directory.
     */
    private void store() {
        try {
            OutputStream out = Resource.writeSession(".token", this);
            out.write(jwt.getToken().getBytes());
            out.close();
        } catch (Exception e) {}
    }
}

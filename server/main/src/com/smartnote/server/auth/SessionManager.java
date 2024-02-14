package com.smartnote.server.auth;

import java.io.File;
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
import com.smartnote.server.Server;
import com.smartnote.server.util.CryptoUtils;
import com.smartnote.server.util.FileUtils;

import spark.Request;

/**
 * <p>Maintains sessions.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
public class SessionManager {
    /**
     * The issuer of the session.
     */
    public static final String ISSUER = "com.smartnote.server";

    /**
     * The name of the session cookie.
     */
    public static final String COOKIE_NAME = "session";

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

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);

    private final Algorithm algorithm; // algorithm for signing
    private final JWTVerifier verifier; // verifier for JWTs

    // executor service for garbage collection
    private final ScheduledExecutorService executorService;

    public SessionManager() {
        String secret = CryptoUtils.randomString(SECRET_LENGTH);

        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).build();

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            LOG.debug("Running session garbage collector");
            forceGc();
        }, GC_INTERVAL, GC_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Gets the algorithm for signing JWTs.
     * 
     * @return The algorithm.
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Gets the verifier for JWTs.
     * 
     * @return The verifier.
     */
    public JWTVerifier getVerifier() {
        return verifier;
    }

    /**
     * Gets the session associated with the request.
     * 
     * @param request The request.
     * @return The session. <code>null</code> if the session is invalid.
     */
    public Session getSession(Request request) {
        DecodedJWT jwt = null;
        
        String auth = request.cookie(COOKIE_NAME);
        if (auth == null)
            return null;

        try {
            jwt = verifier.verify(auth);
        } catch (Exception e) {
            return null;
        }

        return new Session(jwt);
    }

    /**
     * Creates a new session.
     * 
     * @return The session.
     */
    public Session createSession() {
        String user = CryptoUtils.randomString(TOKEN_LENGTH);

        // expiration date
        Instant expr = Instant.now().plusSeconds(SESSION_LENGTH);

        // create the token
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user)
                .withExpiresAt(expr)
                .sign(algorithm);

        Session session = new Session(verifier.verify(token));
        session.store();
        return session;
    }

    /**
     * Checks if a token is valid.
     * 
     * @param token The token.
     * @return <code>true</code> if the token is valid.
     */
    public boolean isTokenValid(String token) {
        try {
            verifier.verify(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Forces garbage collection of sessions.
     */
    public void forceGc() {
        File[] sessionDirs = Server.getServer().getResourceSystem().getSessionDir().toFile().listFiles();
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
}

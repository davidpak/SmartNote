package com.smartnote.server.auth;

import static java.lang.System.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import spark.Request;
import spark.Response;

/**
 * Stores session information.
 * 
 * @author Ethan Vrhel
 */
public class Session {

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);

    /**
     * The issuer of the session.
     */
    public static final String ISSUER = "com.smartnote.server";

    /**
     * The subject of the session.
     */
    public static final String SUBJECT = "com.smartnote.server.session";

    /**
     * The length of the session in seconds.
     */
    public static final long SESSION_LENGTH = 60L * 10L; // 10 minutes

    /**
     * The length of the session secret in bytes.
     */
    public static final int SECRET_LENGTH = 32;

    /**
     * The default secret algorithm.
     */
    public static final String DEFAULT_SECRET_ALGORITHM = new SecureRandom().getAlgorithm();

    private static final Algorithm ALGORITHM; // algorithm for signing
    private static final JWTVerifier VERIFIER; // verifier for JWTs

    static {
        String secret = null;
        String algorithm = DEFAULT_SECRET_ALGORITHM;

        // generate cryptographically secure random string
        try {
            LOG.info("Generating session secret using " + algorithm);

            SecureRandom rand = SecureRandom.getInstance(algorithm);

            byte[] bytes = new byte[SECRET_LENGTH];
            rand.nextBytes(bytes);

            short[] bytesUnsigned = new short[bytes.length];
            for (int i = 0; i < bytes.length; i++)
                bytesUnsigned[i] = (short) (bytes[i] & 0xFF);

            // convert to alphanumeric string
            final String CHAR_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            char[] chars = new char[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                chars[i] = CHAR_ARRAY.charAt(bytesUnsigned[i] % CHAR_ARRAY.length());

                // zero out in memory for security
                bytes[i] = 0;
                bytesUnsigned[i] = 0;
            }

            secret = new String(chars);
        } catch (NoSuchAlgorithmException e1) {
            LOG.error("Algorithm " + algorithm + " was not found");
            exit(1);
        }

        // create algorithm and verifier
        ALGORITHM = Algorithm.HMAC256(secret);
        VERIFIER = JWT.require(ALGORITHM).build();
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
        // expiration date
        Instant expr = Instant.now().plusSeconds(SESSION_LENGTH);

        // create the token
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(SUBJECT)
                .withExpiresAt(expr)
                .sign(ALGORITHM);

        return new Session(VERIFIER.verify(token));
    }

    /**
     * Stores a session in the response.
     * 
     * @param session  The session.
     * @param response The response.
     */
    public static void storeSession(Session session, Response response) {
        response.header("Authorization", session.getJWT().getToken());
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

    private final DecodedJWT jwt;

    private Session(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    /**
     * Gets the JSON web token associated with this session.
     * 
     * @return The JSON web token.
     */
    public DecodedJWT getJWT() {
        return jwt;
    }
}

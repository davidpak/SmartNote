package com.smartnote.server.auth;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smartnote.server.Resource;
import com.smartnote.server.util.CryptoUtils;

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
    public static final long SESSION_LENGTH = 60L * 10L; // 10 minutes

    /**
     * The length of the session secret in bytes.
     */
    public static final int SECRET_LENGTH = 32;

    /**
     * The length of the session token in bytes.
     */
    public static final int TOKEN_LENGTH = 32;

    private static final Algorithm ALGORITHM; // algorithm for signing
    private static final JWTVerifier VERIFIER; // verifier for JWTs

    static {
        // generate cryptographically secure random string
        String secret = CryptoUtils.randomString(SECRET_LENGTH);

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
        String user = CryptoUtils.randomString(TOKEN_LENGTH);

        // expiration date
        Instant expr = Instant.now().plusSeconds(SESSION_LENGTH);

        // create the token
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user)
                .withExpiresAt(expr)
                .sign(ALGORITHM);

        return new Session(VERIFIER.verify(token));
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
}

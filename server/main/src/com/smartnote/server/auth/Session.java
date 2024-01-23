package com.smartnote.server.auth;

import java.time.Instant;
import java.util.Scanner;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smartnote.server.Resource;

import spark.Request;
import spark.Response;

/**
 * Stores session information.
 * 
 * @author Ethan Vrhel
 */
public class Session {
    /**
     * The name of the session secret resource.
     */
    public static final String SESSION_SECRET_NAME = "session_secret.txt";

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

    private static final String SECRET; // session secret
    private static final Algorithm ALGORITHM; // algorithm for signing
    private static final JWTVerifier VERIFIER; // verifier for JWTs

    static {
        Scanner scanner = null;
        String secret = null;

        try {
            // load secret
            scanner = new Scanner(Resource.readPrivate(SESSION_SECRET_NAME));
            secret = scanner.next();
        } catch (Exception e) {
            // manual input
            System.out.println("FAILED TO READ SESSION SECRET");
            System.out.println("Use default (unsafe)? (Y/N)");

            // confirm
            scanner = new Scanner(System.in);
            String input;
            do {
                input = scanner.next();
            } while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n"));

            // exit if no
            if (input.equalsIgnoreCase("n"))
                System.exit(0);

            secret = "unsafe";
        } finally {
            if (scanner != null)
                scanner.close();
            SECRET = secret;
        }

        // create algorithm and verifier
        ALGORITHM = Algorithm.HMAC256(SECRET);
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

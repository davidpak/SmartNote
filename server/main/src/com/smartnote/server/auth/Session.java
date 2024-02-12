package com.smartnote.server.auth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smartnote.server.Server;
import com.smartnote.server.util.FileUtils;

import spark.Request;
import spark.Response;

/**
 * <p>
 * Stores session information. Sessions are implemented using
 * <a href="https://en.wikipedia.org/wiki/JSON_Web_Token">JSON Web Tokens</a>
 * (JWTs). The JWT is stored in the <code>session</code> cookie.
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
    private DecodedJWT jwt; // JSON web token
    private Path sessionDirectory; // session directory
    private Path tokenFile; // file containing the token

    /**
     * Create session from a JSON web token.
     * 
     * @param jwt The JSON web token.
     */
    Session(DecodedJWT jwt) {
        this.jwt = jwt;

        this.sessionDirectory = Server.getServer().getResourceSystem().getSessionDir().resolve(jwt.getSubject());
        this.tokenFile = sessionDirectory.resolve(".token");
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
     * Gets the ID of this session.
     * 
     * @return The ID.
     */
    public String getId() {
        return jwt.getSubject();
    }

    /**
     * Gets the directory associated with this session.
     * 
     * @return The directory.
     */
    public Path getSessionDirectory() {
        return sessionDirectory;
    }

    /**
     * Resolves a path in the session directory.
     * 
     * @param path The path.
     * @return The resolved path. Never <code>null</code>.
     * @throws SecurityException If the path is not in the session directory.
     */
    public Path pathInSession(Path path) throws SecurityException {
        path = sessionDirectory.resolve(path);
        if (!FileUtils.isPathInDirectory(path, sessionDirectory))
            throw new SecurityException("Access denied");
        return path;
    }

    /**
     * Renews the session.
     */
    public void updateSession(SessionManager manager) {
        // expiration date
        Instant expr = Instant.now().plusSeconds(SessionManager.SESSION_LENGTH);

        // create the token
        String token = JWT.create()
                .withIssuer(jwt.getIssuer())
                .withSubject(jwt.getSubject())
                .withExpiresAt(expr)
                .sign(manager.getAlgorithm());

        jwt = manager.getVerifier().verify(token);

        store();
    }

    /**
     * Writes the session token to the response. Will be stored in the
     * <code>session</code> cookie.
     * 
     * @param response The response.
     */
    public void writeToResponse(Response response) {
        Instant expr = jwt.getExpiresAtAsInstant();
        int maxAge = (int) (expr.getEpochSecond() - Instant.now().getEpochSecond());
        Instant expireInstant = Instant.now().plusSeconds(maxAge);

        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        String expires = expireInstant.atZone(ZoneId.of("GMT")).format(formatter);

        String cookie = String.format("%s=%s; Expires=%s; SameSite=Lax", SessionManager.COOKIE_NAME, jwt.getToken(), expires);
        response.header("Set-Cookie", cookie);

        //response.cookie(SessionManager.COOKIE_NAME, jwt.getToken(), maxAge);
    }

    /**
     * Gets the permission associated with this session.
     * 
     * @return The permission. Never <code>null</code>.
     */
    public Permission getPermission() {
        return new SessionPermission(this);
    }

    /**
     * Gets the amount of storage used by this session. Note that this
     * recalculates the size of the session directory every time it is
     * called.
     * 
     * @return The quota used.
     */
    public long getStorageUsage() {
        return FileUtils.getDirectorySize(sessionDirectory.toFile());
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
    public void store() {
        try {
            Files.createDirectories(sessionDirectory);
            Files.write(tokenFile, jwt.getToken().getBytes());
        } catch (Exception e) {
        }
    }
}

package com.smartnote.server.api.v1;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import org.apache.tika.Tika;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.FileUtils;
import com.smartnote.server.util.MIME;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>
 * Uploads a file to the server.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/upload")
public class Upload implements Route {
    /**
     * Directory within session directory to store uploaded files.
     */
    public static final String UPLOAD_DIR = "uploads/";

    @Override
    public Object handle(Request request, Response response) throws Exception {
        ResourceConfig config = Server.getServer().getConfig().getResourceConfig();
    
        response.type("application/json");
        response.type(MIME.JSON);

        SessionManager sessionManager = Server.getServer().getSessionManager();

        // create session
        Session session = sessionManager.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\": \"No session\"}";
        }

        // name of file to upload
        String filename = request.queryParams("name");
        if (filename == null) {
            response.status(400);
            return "{\"message\": \"Name was not specified\"}";
        }

        filename = filename.trim();

        String type = request.contentType();

        if (type == null) {
            String ext = FileUtils.getExtension(filename).toLowerCase();
            type = MIME.fromExtension(ext);
        }

        if (type != null && !ResourceSystem.isSupportedType(type)) {
            response.status(406);
            return "{\"message\": \"Unsupported content type\"}";
        }

        byte[] body = request.bodyAsBytes();
        if (body == null) {
            response.status(400);
            return "{\"message\": \"Missing body\"}";
        }

        // check size
        if (body.length > config.getMaxUploadSize()) {
            response.status(413);
            return "{\"message\": \"File too large\"}";
        }

        long usedQuota = session.getStorageUsage();

        // check quota
        if (usedQuota + body.length > config.getSessionQuota()) {
            response.status(413);
            return "{\"message\": \"Quota exceeded\"}";
        }

        Tika tika = new Tika();
        String contentMIME = tika.detect(body);
        if (!ResourceSystem.isSupportedType(contentMIME)) {
            response.status(406);
            return "{\"message\": \"Unsupported file type\"}";
        }

        // if the MIME types don't match, change the extension
        //if (!contentMIME.equals(inferredMIME))
        //    filename = FileUtils.removeExtension(filename) + "." + MIME.toExtension(contentMIME);

        filename = UPLOAD_DIR + filename;

        Permission permission = session.getPermission();

        ResourceSystem system = Server.getServer().getResourceSystem();
        Resource resource = null;

        // find resource
        String path = ResourceSystem.inSession(filename);
        try {
            resource = system.findResource(path, permission);
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\": \"Access denied\"}";
        } catch (InvalidPathException e) {
            response.status(400);
            return "{\"message\": \"Invalid path\"}";
        } catch (NoSuchResourceException e) {
            // ignore
        } catch (IOException e) {
            response.status(500);
            return "{\"message\": \"Could open resource\"}";
        }

        // write
        OutputStream out = null;
        try {
            out = resource.openOutputStream();
            out.write(request.bodyAsBytes());
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\":\"Access denied\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\":\"Could not write resource\"}";
        } finally {
            if (out != null)
                out.close();
        }

        session.updateSession(sessionManager);
        session.writeToResponse(response);

        JsonObject obj = new JsonObject();
        obj.addProperty("message", "File uploaded");
        obj.addProperty("name", system.getActualPath(path));

        return new Gson().toJson(obj);
    }
}

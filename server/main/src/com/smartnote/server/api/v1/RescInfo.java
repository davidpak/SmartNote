package com.smartnote.server.api.v1;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.Server;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>Provides information about the server's resource system.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.resource.ResourceConfig
 * @see com.smartnote.server.resource.ResourceSystem
 */
@ServerRoute(path = "/api/v1/rescinfo", method=MethodType.GET)
public class RescInfo implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        ResourceConfig config = Server.getServer().getConfig().getResourceConfig();
        response.type("application/json");

        JsonObject obj = new JsonObject();
        obj.addProperty("sessionQuota", config.getSessionQuota());
        obj.addProperty("maxUploadSize", config.getMaxUploadSize());

        JsonArray supportedTypes = new JsonArray();
        for (String type : ResourceSystem.SUPPORTED_MIME_TYPES)
            supportedTypes.add(type);
        obj.add("supportedTypes", supportedTypes);
        
        JsonArray authorities = new JsonArray();
        for (String auth : ResourceSystem.AUTHORITIES)
            authorities.add(auth);
        obj.add("authorities", authorities);

        Gson gson = new Gson();
        String json = gson.toJson(obj);

        return json;
    }
    
}

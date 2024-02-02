package com.smartnote.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.api.v1.RescInfo;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.testing.BaseRoute;

/**
 * <p>Tests the <code>rescinfo</code> RPC.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.api.v1.RescInfo
 */
public class RescInfoTest extends BaseRoute {
    private RescInfo rescInfo;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rescInfo = new RescInfo();
    }

    @Test
    public void testRescInfoBasic() throws Exception {
        ResourceConfig config = Server.getServer().getConfig().getResourceConfig();
        doApiTest(rescInfo, 200);

        // check response json has all fields
        JsonObject json = responseJson();
        JsonElement element;

        JsonPrimitive sessionQuota;
        element = json.get("sessionQuota");
        assertNotNull(element);
        assertTrue(element.isJsonPrimitive());
        sessionQuota = element.getAsJsonPrimitive();
        assertTrue(sessionQuota.isNumber());
        assertEquals(config.getSessionQuota(), sessionQuota.getAsLong());

        JsonPrimitive maxUploadSize;
        element = json.get("maxUploadSize");
        assertNotNull(element);
        assertTrue(element.isJsonPrimitive());
        maxUploadSize = element.getAsJsonPrimitive();
        assertTrue(maxUploadSize.isNumber());
        assertEquals(config.getMaxUploadSize(), maxUploadSize.getAsLong());

        element = json.get("supportedTypes");
        assertNotNull(element);
        assertTrue(element.isJsonArray());

        element = json.get("authorities");
        assertNotNull(element);
        assertTrue(element.isJsonArray());
    }
}
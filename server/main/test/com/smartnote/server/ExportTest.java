package com.smartnote.server;

import static org.junit.Assert.*;

import java.io.OutputStream;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.api.v1.Export;
import com.smartnote.server.auth.Session;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.testing.BaseRoute;

/**
 * <p>Tests the <code>export</code> RPC.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.api.v1.Export
 */
public class ExportTest extends BaseRoute {
    public static final String SUMMARY_FILE_LINES[] = {
        "# Summary",
        "## About",
        "This is some example markdown that will be exported"
    };
    public static final String SUMMARY_RESOURCE_NAME = ResourceSystem.SESSION_AUTH + ":summary.md";
    
    public static final String SUMMARY_FILE_DATA;

    static {
        StringBuilder builder = new StringBuilder();
        for (String line : SUMMARY_FILE_LINES) {
            builder.append(line);
            builder.append('\n');
        }
        SUMMARY_FILE_DATA = builder.toString();
    }

    private Export export;
    private Session session;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.export = new Export();
        session = getSession(activateSession());

        activateSession();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(SUMMARY_RESOURCE_NAME, session.getPermission());
        OutputStream out = resource.openOutputStream();
        out.write(SUMMARY_FILE_DATA.getBytes());
        out.close();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Tests the export API.
     * 
     * @param code The expected response code.
     * @param name The name of the resource to export.
     * @param format The format to export to.
     * @return The JSON response.
     * @throws Exception If an error occurs.
     */
    public JsonObject doExportTest(int code, String name, String format) throws Exception {
        JsonObject options = new JsonObject();

        if (name != null)
            options.addProperty("name", name);

        if (format != null)
            options.addProperty("type", format);

        setRequestBody(getGson().toJson(options));

        doApiTest(export, code);

        return responseJson();
    }

    /**
     * Tests the export API and returns the resource, specifically for local
     * exports (such as JSON, RTF, etc.).
     * 
     * @param code The expected response code.
     * @param name The name of the resource to export.
     * @param format The format to export to. Should be a local format.
     * @return The resource.
     * @throws Exception If an error occurs.
     */
    public Resource doLocalExportTest(int code, String name, String format) throws Exception {
        JsonObject json = doExportTest(code, name, format);

        JsonElement nameElement = json.get("name");
        assertNotNull(nameElement);
        assertTrue(nameElement.isJsonPrimitive());
        JsonPrimitive namePrimitive = nameElement.getAsJsonPrimitive();
        assertTrue(namePrimitive.isString());

        String resourceName = namePrimitive.getAsString();

        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(resourceName, session.getPermission());
        assertNotNull(resource);

        return resource;
    }

    @Test
    public void testExportJSON() throws Exception {
        Resource resource = doLocalExportTest(200, SUMMARY_RESOURCE_NAME, "json");
        String jsonString = resource.readToString();

        getGson().fromJson(jsonString, JsonObject.class); // make sure it's valid JSON
    }

    @Test
    public void testExportRTF() throws Exception {
        Resource resource = doLocalExportTest(200, SUMMARY_RESOURCE_NAME, "rtf");
        String rtfString = resource.readToString();

        // make sure the data is valid RTF
        assertTrue(rtfString.startsWith("{\\rtf"));
    }

    @Test
    public void testExportNotion() throws Exception {
        doExportTest(200, SUMMARY_RESOURCE_NAME, "notion");
    }

    @Test
    public void testInvalidExporter() throws Exception {
        doExportTest(400, SUMMARY_RESOURCE_NAME, "not_a_service");
    }

    @Test
    public void testNoName() throws Exception {
        doExportTest(400, null, "json");
    }

    @Test
    public void testNoType() throws Exception {
        doExportTest(400, SUMMARY_RESOURCE_NAME, null);
    }

    @Test
    public void testNoTypeAndName() throws Exception {
        doExportTest(400, null, null);
    }

    @Test
    public void testMissingResource() throws Exception {
        doExportTest(404, "session:missing.txt", "json");
    }

    @Test
    public void testNoSession() throws Exception {
        deactivateSession();
        doExportTest(401, SUMMARY_RESOURCE_NAME, "json");
    }

    @Test
    public void testInvalidName() throws Exception {
        doExportTest(400, "invalidname", "json");
    }
}

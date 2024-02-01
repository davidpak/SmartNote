package com.smartnote.server;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.api.v1.Export;
import com.smartnote.server.auth.Session;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.testing.RouteTest;

import spark.Response;

public class ExportTest extends RouteTest {
    public static final String SUMMARY_FILE_NAME = "summary.txt";
    public static final String SUMMARY_FILE_LINES[] = {
        "# Summary",
        "## About",
        "This is some example markdown that will be exported"
    };
    public static final String SUMMARY_RESOURCE_NAME = ResourceSystem.SESSION_AUTH + ":" + SUMMARY_FILE_NAME;

    private Export export;
    private Session session;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.export = new Export();
        session = getSession(activateSession());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public Response doApiTest(int code) throws Exception {
        // Write the summary file to the session
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(SUMMARY_RESOURCE_NAME, session.getPermission());
        OutputStream out = resource.openOutputStream();
        for (String line : SUMMARY_FILE_LINES) {
            out.write(line.getBytes());
            out.write('\n');
        }
        out.close();

        Response response = doApiTest(export, code);
    
        return response;
    }

    @Test
    public void testExportJSON() throws Exception {
        JsonObject options = new JsonObject();
        options.addProperty("name", SUMMARY_RESOURCE_NAME);
        options.addProperty("type", "json");

        setRequestBody(getGson().toJson(options));

        doApiTest(200);

        JsonObject json = responseJson();
        JsonElement nameElement = json.get("name");
        assertNotNull(nameElement);
        assertTrue(nameElement.isJsonPrimitive());
        JsonPrimitive namePrimitive = nameElement.getAsJsonPrimitive();
        assertTrue(namePrimitive.isString());

        String resourceName = namePrimitive.getAsString();

        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(resourceName, session.getPermission());
        assertNotNull(resource);

        InputStream in = resource.openInputStream();
        String data = new String(in.readAllBytes());
        in.close();

        // make sure the data is valid JSON
        getGson().fromJson(data, JsonObject.class);
    }

    @Test
    public void testExportRTF() throws Exception {
        fail("Not implemented");
    }

    @Test
    public void testExportNotion() throws Exception {
        fail("Not implemented");
    }

    @Test
    public void testInvalidExporter() throws Exception {
        JsonObject options = new JsonObject();
        options.addProperty("name", SUMMARY_RESOURCE_NAME);
        options.addProperty("type", "not_a_service");

        setRequestBody(getGson().toJson(options));

        doApiTest(400);
    }

    @Test
    public void testNoName() throws Exception {
        JsonObject options = new JsonObject();
        options.addProperty("type", "json");

        setRequestBody(getGson().toJson(options));

        doApiTest(400);
    }

    @Test
    public void testNoType() throws Exception {
        JsonObject options = new JsonObject();
        options.addProperty("name", SUMMARY_RESOURCE_NAME);

        setRequestBody(getGson().toJson(options));

        doApiTest(400);
    }

    @Test
    public void testNoSession() throws Exception {
        deactivateSession();

        JsonObject options = new JsonObject();
        options.addProperty("name", SUMMARY_RESOURCE_NAME);
        options.addProperty("type", "json");

        setRequestBody(getGson().toJson(options));

        doApiTest(401);
    }
}

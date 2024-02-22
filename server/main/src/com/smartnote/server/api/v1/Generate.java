package com.smartnote.server.api.v1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.smartnote.server.GeneratorConfig;
import com.smartnote.server.Server;
import com.smartnote.server.auth.Session;
import com.smartnote.server.auth.SessionManager;
import com.smartnote.server.format.ParsedMarkdown;
import com.smartnote.server.resource.NoSuchResourceException;
import com.smartnote.server.resource.Resource;
import com.smartnote.server.resource.ResourceSystem;
import com.smartnote.server.util.MethodType;
import com.smartnote.server.util.ServerRoute;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * <p>
 * Generates summaries from uploaded files.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.auth.Session
 */
@ServerRoute(method = MethodType.POST, path = "/api/v1/generate")
public class Generate implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        SessionManager sessionManager = Server.getServer().getSessionManager();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        GeneratorConfig generatorConfig = Server.getServer().getConfig().getGeneratorConfig();
        String summarizer = generatorConfig.getSummarizer();

        long startTime = System.currentTimeMillis();

        Session session = sessionManager.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\":\"No session\"}";
        }

        Permission permission = session.getPermission();

        Resource outResource;
        try {
            if (generatorConfig.isDebug())
                outResource = resourceSystem.findResource(generatorConfig.getDebugResource(), permission);
            else
                outResource = generate(summarizer, request.body(), session, permission);
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "{\"message\":" + e.getMessage() + "\"}";
        } catch (NoSuchResourceException e) {
            response.status(404);
            return "{\"message\":\"Resource not found\"}";
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\":\"Permission denied\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\":\"Generation failed\"}";
        } catch (InterruptedException e) {
            response.status(500);
            return "{\"message\":\"Data generation interrupted\"}";
        }

        String markdownString;
        ParsedMarkdown md;
        InputStream in = null;
        try {
            in = outResource.openInputStream();
            markdownString = new String(in.readAllBytes());
            md = ParsedMarkdown.parse(markdownString);
        } catch (SecurityException e) {
            response.status(403);
            return "{\"message\":\"Permission denied\"}";
        } catch (NoSuchResourceException e) {
            response.status(404);
            return "{\"message\":\"Resource not found\"}";
        } catch (IOException e) {
            response.status(500);
            return "{\"message\":\"Internal server error\"}";
        } catch (IllegalArgumentException e) {
            response.status(500);
            return "{\"message\":\"Generated content is invalid\"}";
        } finally {
            if (in != null)
                in.close();
        }

        long endTime = System.currentTimeMillis();

        JsonObject resObject = new JsonObject();
        resObject.addProperty("name", outResource.getName());
        resObject.addProperty("time", (endTime - startTime) / 1000.0);
        resObject.add("result", md.writeJSON());
        resObject.addProperty("markdown", markdownString);

        return new Gson().toJson(resObject);
    }

    private Resource generate(String summarizer, String body, Session session,
            Permission permission)
            throws IllegalArgumentException, NoSuchResourceException, SecurityException, IOException,
            InterruptedException {
        JsonObject generateJson = new Gson().fromJson(body, JsonObject.class);

        JsonObject generalOptions = getObjectOrNull(generateJson, "general");
        if (generalOptions == null)
            throw new IllegalArgumentException("Missing field general");

        JsonArray files = getArrayOrNull(generalOptions, "files");
        if (files == null)
            throw new IllegalArgumentException("Missing field general.files");

        if (files.size() == 0)
            throw new IllegalArgumentException("No files specified");

        JsonObject llmOptions = getObjectOrNull(generateJson, "llm");
        if (llmOptions == null)
            throw new IllegalArgumentException("Missing field llm");

        double verbosity = getNumberOrDefault(llmOptions, "verbosity", 0.5);
        boolean generalOverview = getBooleanOrFalse(llmOptions, "generalOptions");
        boolean keyConcepts = getBooleanOrFalse(llmOptions, "keyConcepts");
        boolean sectionBySection = getBooleanOrFalse(llmOptions, "sectionBySection");
        boolean additionalInformation = getBooleanOrFalse(llmOptions, "additionalInformation");
        boolean helpfulVocabulary = getBooleanOrFalse(llmOptions, "helpfulVocabulary");
        boolean explainToFifthGrader = getBooleanOrFalse(llmOptions, "explainToFifthGrader");
        boolean conclusion = getBooleanOrFalse(llmOptions, "conclusion");

        String first = files.get(0).getAsString();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(first, permission);

        GeneratorConfig generatorConfig = Server.getServer().getConfig().getGeneratorConfig();

        final String inName = resource.getName();
        final String outName = "session:output.md";

        Resource outResource = resourceSystem.findResource(outName, permission);

        List<String> command = new ArrayList<>();
        command.add("python3");
        command.add("--env");
        command.add(generatorConfig.getEnv());
        command.add("verbose");
        command.add(Double.toString(verbosity));
        if (!generalOverview)
            command.add("--no_general_overview");
        if (!keyConcepts)
            command.add("--no_key_concepts");
        if (!sectionBySection)
            command.add("--no_section_by_section");
        if (!additionalInformation)
            command.add("--no_additional_information");
        if (!helpfulVocabulary)
            command.add("--no_helpful_vocabulary");
        if (!explainToFifthGrader)
            command.add("--no_explain_to_5th_grader");
        if (!conclusion)
            command.add("--no_conclusion");

        command.add(inName);
        command.add(outName);

        String[] commandArray = command.toArray(new String[command.size()]);
        ProcessBuilder pb = new ProcessBuilder(commandArray);

        Process p = pb.start();

        int exitCode = p.waitFor();

        if (exitCode != 0)
            throw new IOException("Summarizer exited with non-zero exit code");

        return outResource;
    }
}

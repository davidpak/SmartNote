package com.smartnote.server.api.v1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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
import com.smartnote.server.util.FileUtils;
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

        JsonObject generateJson = new Gson().fromJson(request.body(), JsonObject.class);

        Permission permission = session.getPermission();

        // generate the summary
        Resource outResource;
        try {
            if (generatorConfig.isDebug())
                outResource = resourceSystem.findResource(generatorConfig.getDebugResource(), permission);
            else {
                summarizer = FileUtils.getCanonicalPath(summarizer);
                outResource = generate(summarizer, generateJson, session, permission);
            }
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

        JsonObject general = generateJson.getAsJsonObject("general");
        boolean includeJson = getBooleanOrFalse(general, "includeJson");
        boolean includeMarkdown = getBooleanOrFalse(general, "includeMarkdown");

        // parse the generated markdown
        String markdownString = null;
        ParsedMarkdown md = null;
        if (includeJson || includeMarkdown) {
            InputStream in = null;
            try {
                in = outResource.openInputStream();
                markdownString = new String(in.readAllBytes());

                if (includeJson)
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
        }

        long endTime = System.currentTimeMillis();

        // return the result
        JsonObject resObject = new JsonObject();
        resObject.addProperty("name", outResource.getName());
        resObject.addProperty("time", (endTime - startTime) / 1000.0);

        if (md != null)
            resObject.add("result", md.writeJSON());

        if (markdownString != null)
            resObject.addProperty("markdown", markdownString);

        return new Gson().toJson(resObject);
    }

    /**
     * Generates a summary from the given body.
     * 
     * @param summarizer The summarizer to use.
     * @param json       The generation options.
     * @param session    The session.
     * @param permission The permission.
     * @return The generated resource.
     * @throws IllegalArgumentException If the body is invalid.
     * @throws NoSuchResourceException  If the resource does not exist.
     * @throws SecurityException        If the user does not have permission to
     *                                  access the resource.
     * @throws IOException              If an I/O error occurs.
     * @throws InterruptedException     If the thread is interrupted while waiting
     *                                  for the summarizer to finish.
     */
    private Resource generate(String summarizer, JsonObject json, Session session,
            Permission permission)
            throws IllegalArgumentException, NoSuchResourceException, SecurityException, IOException,
            InterruptedException {
        JsonObject generalOptions = getObjectOrNull(json, "general");
        if (generalOptions == null)
            throw new IllegalArgumentException("Missing field general");

        JsonArray files = getArrayOrNull(generalOptions, "files");
        if (files == null)
            throw new IllegalArgumentException("Missing field general.files");

        if (files.size() == 0)
            throw new IllegalArgumentException("No files specified");

        JsonObject llmOptions = getObjectOrNull(json, "llm");
        if (llmOptions == null)
            throw new IllegalArgumentException("Missing field llm");

        // Get all the options, defaulting to true
        double verbosity = getNumberOrDefault(llmOptions, "verbosity", 1.0);
        boolean generalOverview = getBooleanOrTrue(llmOptions, "generalOptions");
        boolean keyConcepts = getBooleanOrTrue(llmOptions, "keyConcepts");
        boolean sectionBySection = getBooleanOrTrue(llmOptions, "sectionBySection");
        boolean additionalInformation = getBooleanOrTrue(llmOptions, "additionalInformation");
        boolean helpfulVocabulary = getBooleanOrTrue(llmOptions, "helpfulVocabulary");
        boolean explainToFifthGrader = getBooleanOrTrue(llmOptions, "explainToFifthGrader");
        boolean conclusion = getBooleanOrTrue(llmOptions, "conclusion");

        String first = files.get(0).getAsString();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        Resource resource = resourceSystem.findResource(first, permission);

        GeneratorConfig generatorConfig = Server.getServer().getConfig().getGeneratorConfig();

        final String inPath = resource.getPath().toString();
        final String outName = "session:output.md"; // always output to this file

        Resource outResource = resourceSystem.findResource(outName, permission);
        String outPath = outResource.getPath().toString();

        String envPath = FileUtils.getCanonicalPath(generatorConfig.getEnv());

        // get directory of summarizer
        Path summarizerPath = new File(summarizer).toPath();
        File summarizerDir = summarizerPath.getParent().toFile();

        // build command
        List<String> command = new ArrayList<>();
        command.add(generatorConfig.getPython());
        command.add(summarizer);
        command.add("--env");
        command.add(envPath);
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

        command.add(inPath);
        command.add(outPath);

        String[] commandArray = command.toArray(new String[command.size()]);

        // build process
        ProcessBuilder pb = new ProcessBuilder(commandArray);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.directory(summarizerDir);

        // start and wait for process to finish
        Process p = pb.start();
        int exitCode = p.waitFor();

        if (exitCode != 0)
            throw new IOException("Summarizer exited with non-zero exit code");

        return outResource;
    }
}

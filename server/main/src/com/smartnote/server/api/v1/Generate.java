package com.smartnote.server.api.v1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Path;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import com.smartnote.server.util.MIME;
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

    public static final String OUTPUT_RESOURCE = "session:output.md";

    private static final Logger LOG = LoggerFactory.getLogger(Generate.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {
        SessionManager sessionManager = Server.getServer().getSessionManager();
        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        GeneratorConfig generatorConfig = Server.getServer().getConfig().getGeneratorConfig();
        String summarizer = generatorConfig.getSummarizer();

        long startTime = System.currentTimeMillis();

        response.type(MIME.JSON);

        Session session = sessionManager.getSession(request);
        if (session == null) {
            response.status(401);
            return "{\"message\":\"No session\"}";
        }

        Gson gson = new Gson();
        JsonObject generateJson = gson.fromJson(request.body(), JsonObject.class);

        JsonObject result = new JsonObject();
        Permission permission = session.getPermission();
        String message = null;

        // generate the summary
        Resource outResource;
        try {
            if (generatorConfig.isDebug()) {
                LOG.info("Debug mode enabled, using debug resource");
                outResource = resourceSystem.findResource(generatorConfig.getDebugResource(), permission);
            } else {
                StringBuilder messageBuilder = new StringBuilder();
                summarizer = FileUtils.getCanonicalPath(summarizer);
                
                LOG.info("Generating summary with summarizer: " + summarizer);
                outResource = generate(summarizer, generateJson, session, permission, messageBuilder);

                message = messageBuilder.toString();
                if (message.length() == 0) message = null;
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Invalid generation options", e);

            response.status(400);
            result.addProperty("message", "Invalid generation options: " + e.getMessage());
            if (message != null)
                result.addProperty("extended", message);
            return gson.toJson(result);
        } catch (NoSuchResourceException e) {
            LOG.warn("Resource not found", e);
            response.status(404);
            result.addProperty("message", "Resource not found");
            return gson.toJson(result);
        } catch (SecurityException e) {
            LOG.warn("Permission denied", e);

            response.status(403);
            result.addProperty("message", "Permission denied");
            if (message != null)
                result.addProperty("extended", message);
            return gson.toJson(result);
        } catch (IOException e) {
            LOG.warn("IO error", e);
            response.status(500);
            result.addProperty("message", "Internal server error");
            return gson.toJson(result);
        } catch (InterruptedException e) {
            LOG.warn("Generation interrupted", e);
            response.status(500);
            result.addProperty("message", "Generation interrupted");
            return gson.toJson(result);
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
                LOG.warn("Permission denied", e);
                response.status(403);
                result.addProperty("message", "Permission denied");
                return gson.toJson(result);
            } catch (NoSuchResourceException e) {
                LOG.warn("Resource not found", e);
                response.status(404);
                result.addProperty("message", "Resource not found");
                return gson.toJson(result);
            } catch (IOException e) {
                LOG.warn("IO error", e);
                result.addProperty("message", "Internal server error");
                return gson.toJson(result);
            } catch (IllegalArgumentException e) {
                LOG.warn("Generated content is invalid", e);
                response.status(500);
                result.addProperty("message", "Generated content is invalid");
                return gson.toJson(result);
            } finally {
                if (in != null)
                    in.close();
            }
        }

        long endTime = System.currentTimeMillis();

        // return the result
        result.addProperty("name", outResource.getName());
        result.addProperty("time", (endTime - startTime) / 1000.0);

        if (md != null)
            result.add("result", md.writeJSON());

        if (markdownString != null)
            result.addProperty("markdown", markdownString);

        LOG.info("Generated summary: " + outResource.getName() + " in " + result.get("time").getAsDouble() + "s");
        return gson.toJson(result);
    }

    /**
     * Generates a summary from the given body.
     * 
     * @param summarizer The summarizer to use.
     * @param json       The generation options.
     * @param session    The session.
     * @param permission The permission.
     * @param message    The message to append to.
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
            Permission permission, StringBuilder message)
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

        if (!generalOverview && !keyConcepts && !sectionBySection && !additionalInformation
                && !helpfulVocabulary && !explainToFifthGrader && !conclusion)
            throw new IllegalArgumentException("Need at least one summarization option to include");

        ResourceSystem resourceSystem = Server.getServer().getResourceSystem();
        GeneratorConfig generatorConfig = Server.getServer().getConfig().getGeneratorConfig();

        // find all the input files
        List<String> inputFiles = new ArrayList<>();
        for (JsonElement e : files) {
            String name = e.getAsString();
            
            if (name.startsWith("http://") || name.startsWith("https://")) {
                URL url = new URL(name);
                if (!url.getHost().equals("www.youtube.com"))
                    throw new IllegalArgumentException("A URL was specified, but was not a YouTube video");

                inputFiles.add(name);
                continue;
            }

            Resource resource = resourceSystem.findResource(name, permission);
            if (resource == null)
                throw new NoSuchResourceException("Input resource not found: " + name);
            inputFiles.add(FileUtils.getCanonicalPath(resource.getPath().toString()));
        }

        String envPath = FileUtils.getCanonicalPath(generatorConfig.getEnv());

        Resource outResource = resourceSystem.findResource(OUTPUT_RESOURCE, permission);
        String outPath = FileUtils.getCanonicalPath(outResource.getPath().toString());

        // get directory of summarizer
        Path summarizerPath = new File(summarizer).toPath();
        File summarizerDir = summarizerPath.getParent().toFile();

        // build command
        List<String> command = new ArrayList<>();
        command.add(generatorConfig.getPython());
        command.add(summarizer);
        command.add("--env");
        command.add(envPath);
        command.add("--verbose");
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

        command.add("--out");
        command.add(outPath);

        command.addAll(inputFiles);

        String[] commandArray = command.toArray(new String[command.size()]);

        // build process
        ProcessBuilder pb = new ProcessBuilder(commandArray);
        //pb.redirectErrorStream(true);
        //pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(Redirect.PIPE);
        pb.redirectOutput(Redirect.INHERIT);
        pb.directory(summarizerDir);

        // start and wait for process to finish
        Process p = pb.start();
        InputStream in = p.getErrorStream();
        
        // read output from stderr
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                System.out.write(buffer, 0, bytesRead);
                message.append(new String(buffer, 0, bytesRead));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }

        int exitCode = p.waitFor();
        if (exitCode != 0)
            throw new IOException("Summarizer exited with non-zero exit code");

        return outResource;
    }
}

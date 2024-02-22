package com.smartnote.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.export.NotionConfig;
import com.smartnote.server.resource.ResourceConfig;
import com.smartnote.server.util.AbstractConfig;
import com.smartnote.server.util.FileUtils;

/**
 * <p>
 * Stores configuration information for the server.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.Server
 */
public class Config extends AbstractConfig {

    /**
     * Location of the config file.
     */
    public static final String CONFIG_FILE = "config.json";

    /**
     * Loads the config file.
     * 
     * @return The config file.
     * @throws IOException         If an I/O error occurs while reading the file.
     * @throws JsonSyntaxException If the file is not valid JSON.
     */
    public static Config loadConfig() throws IOException, JsonSyntaxException {
        String data = null;
        try {
            data = FileUtils.readFile(CONFIG_FILE);
        } catch (Exception e) {
            throw new IOException("Could not load config file", e);
        }

        Gson gson = new Gson();
        Config config = new Config();

        JsonObject object = gson.fromJson(data, JsonObject.class);
        config.loadJSON(object);
        return config;
    }

    /**
     * Writes the config file.
     * 
     * @param config The config file to write.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public static void writeConfig(Config config) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String data = gson.toJson(config);
        FileUtils.writeFile(CONFIG_FILE, data);
    }

    private ServerConfig server;
    private ResourceConfig resource;
    private NotionConfig notion;
    private GeneratorConfig generator;

    /**
     * Creates a new Config object with default values.
     */
    public Config() {
        this.server = new ServerConfig();
        this.resource = new ResourceConfig();
        this.notion = new NotionConfig();
        this.generator = new GeneratorConfig();
    }

    /**
     * Gets the server configuration.
     * 
     * @return The server configuration
     */
    public ServerConfig getServerConfig() {
        return server;
    }

    /**
     * Gets the resource configuration.
     * 
     * @return The resource configuration
     */
    public ResourceConfig getResourceConfig() {
        return resource;
    }

    /**
     * Gets the Notion configuration.
     * 
     * @return The Notion configuration
     */
    public NotionConfig getNotionConfig() {
        return notion;
    }

    /**
     * Gets the generator configuration.
     * 
     * @return The generator configuration
     */
    public GeneratorConfig getGeneratorConfig() {
        return generator;
    }

    @Override
    public void validate() throws IllegalStateException {
        server.validate();
        resource.validate();
        notion.validate();
        generator.validate();
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler(server);
        parser.addHandler(resource);
        parser.addHandler(notion);
        parser.addHandler(generator);
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        server.writeJSON(json);
        resource.writeJSON(json);
        notion.writeJSON(json);
        generator.writeJSON(json);
        return json;
    }

    @Override
    public void loadJSON(JsonObject object) {
        if (object.has("server"))
            server.loadJSON(object.getAsJsonObject("server"));

        if (object.has("resource"))
            resource.loadJSON(object.getAsJsonObject("resource"));

        if (object.has("notion"))
            notion.loadJSON(object.getAsJsonObject("notion"));

        if (object.has("generator"))
            generator.loadJSON(object.getAsJsonObject("generator"));
    }
}

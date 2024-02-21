package com.smartnote.server;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.JsonObject;
import com.smartnote.server.util.AbstractConfig;

public class GeneratorConfig extends AbstractConfig {

    /**
     * The default summarizer for the generator.
     */
    public static final String DEFAULT_SUMMARIZER = "scripts/src/summarize.py";

    /**
     * The default environment file for the generator.
     */
    public static final String DEFAULT_ENV = "private/.env";

    public static final String DEFAULT_OUTPUT_RESOURCE = "public:output.md";

    /**
     * The default debug resource for the generator.
     */
    public static final String DEFAULT_DEBUG_RESOURCE = "public:output.md";

    private String summarizer;
    private String env;
    private String outputResource;
    private boolean debug;
    private String debugResource;

    public GeneratorConfig() {
        this.summarizer = DEFAULT_SUMMARIZER;
        this.env = DEFAULT_ENV;
        this.debug = false;
        this.debugResource = DEFAULT_DEBUG_RESOURCE;
    }

    public String getSummarizer() {
        return summarizer;
    }

    public String getEnv() {
        return env;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getDebugResource() {
        return debugResource;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        System.out.println("generator.summarizer=`" + summarizer + "`");
        System.out.println("generator.env=`" + env + "`");
        System.out.println("generator.debug=" + debug);

        if (debug && debugResource == null)
            throw new IllegalArgumentException(
                    "generator.debugResource cannot be null if generator debugging is enabled");

        System.out.println("generator.debugResource=`" + debugResource + "`");
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("summarizer", summarizer);
        json.addProperty("env", env);
        json.addProperty("debug", debug);
        json.addProperty("debugResource", debugResource);
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        summarizer = getStringOrNull(json, "summarizer");
        if (summarizer == null)
            summarizer = DEFAULT_SUMMARIZER;

        env = getStringOrNull(json, "env");
        if (env == null)
            env = DEFAULT_ENV;

        debug = getBooleanOrFalse(json, "debug");

        debugResource = getStringOrNull(json, "debugResource");
    }
}

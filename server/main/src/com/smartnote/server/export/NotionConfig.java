package com.smartnote.server.export;

import static com.smartnote.server.util.JSONUtil.*;

import com.google.gson.JsonObject;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.util.AbstractConfig;

public class NotionConfig extends AbstractConfig {
    private String clientId;
    private String secret;

    private boolean allowRemoteIntegrations;

    public NotionConfig() {
        clientId = null;
        secret = null;
        allowRemoteIntegrations = false;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecret() {
        return secret;
    }

    public boolean allowRemoteIntegrations() {
        return allowRemoteIntegrations;
    }

    @Override
    public void validate() throws IllegalStateException {
        if (clientId == null)
            throw new IllegalStateException("Notion client ID not set");

        if (secret == null)
            throw new IllegalStateException("Notion secret not set");
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        parser.addHandler("notionClientId", (p, a) -> {
            clientId = p.next();
        }, "c");

        parser.addHandler("notionSecret", (p, a) -> {
            secret = p.next();
        }, "s");

        parser.addHandler("allowRemoteIntegrations", (p, a) -> {
            allowRemoteIntegrations = true;
        }, "a");
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("clientId", clientId);
        json.addProperty("secret", secret);
        json.addProperty("allowRemoteIntegrations", allowRemoteIntegrations);
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        clientId = getStringOrNull(json, "clientId");
        secret = getStringOrNull(json, "secret");
        allowRemoteIntegrations = getBooleanOrFalse(json, "allowRemoteIntegrations");
    }
}

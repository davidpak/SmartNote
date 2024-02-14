package com.smartnote.server.export;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.smartnote.server.cli.CommandLineParser;
import com.smartnote.server.util.AbstractConfig;

public class NotionConfig extends AbstractConfig {
    private String clientId;
    private String secret;

    public NotionConfig() {
        clientId = null;
        secret = null;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecret() {
        return secret;
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
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        json.addProperty("clientId", clientId);
        json.addProperty("secret", secret);
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        JsonElement e;
        JsonPrimitive p;

        JsonObject oauth = json.getAsJsonObject("oauth");
        if (oauth != null) {
            e = oauth.get("clientId");
            if (e != null && e.isJsonPrimitive()) {
                p = e.getAsJsonPrimitive();
                if (p.isString()) {
                    clientId = p.getAsString();
                }
            }

            e = oauth.get("secret");
            if (e != null && e.isJsonPrimitive()) {
                p = e.getAsJsonPrimitive();
                if (p.isString()) {
                    secret = p.getAsString();
                }
            }
        }
    }
}

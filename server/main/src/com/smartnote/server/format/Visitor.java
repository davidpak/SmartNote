package com.smartnote.server.format;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class Visitor {
    public abstract void visit(JsonPrimitive primitive);
    public abstract void visit(JsonObject object);
    public abstract void visit(JsonArray array);

    public void visitChildren(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement e : array)
                accept(e);
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (String key : object.keySet())
                accept(object.get(key));
        }
    }

    public void accept(JsonElement element) {
        if (element.isJsonPrimitive()) {
            visit(element.getAsJsonPrimitive());
        } else if (element.isJsonObject()) {
            visit(element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            visit(element.getAsJsonArray());
        }  
    }
}

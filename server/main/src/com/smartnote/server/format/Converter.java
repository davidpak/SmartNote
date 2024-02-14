package com.smartnote.server.format;

import com.google.gson.JsonObject;

public interface Converter<T> {
    T convert(JsonObject document);
}

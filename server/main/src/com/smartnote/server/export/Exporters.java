package com.smartnote.server.export;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.smartnote.server.util.ReflectionUtils;

public final class Exporters {
    
    private static final Map<String, Exporter> EXPORTERS;

    static {
        EXPORTERS = new HashMap<>();
        
        Enumeration<Class<?>> exportClasses;
        try {
            exportClasses = ReflectionUtils.getClasses("com.smartnote.server.export");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Check if the class is an Exporter and if it has the ExporterInfo annotation
        while (exportClasses.hasMoreElements()) {
            Class<?> clazz = exportClasses.nextElement();
            if (Exporter.class.isAssignableFrom(clazz)) {
                if (clazz.isAnnotationPresent(ExporterInfo.class)) {
                    ExporterInfo info = clazz.getAnnotation(ExporterInfo.class);
                    try {
                        Constructor<?> constructor = clazz.getConstructor();
                        Exporter exporter = (Exporter) constructor.newInstance();
                        EXPORTERS.put(info.name(), exporter);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static Exporter getExporter(String name) {
        return EXPORTERS.get(name);
    }

    private Exporters() {}
}

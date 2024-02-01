package com.smartnote.server.export;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Maintains <code>Exporter</code> instances that can be used to
 * export summaries to different formats or locations. An instance of
 * this class is available through the <code>getExporters</code>
 * method. Retrieve an <code>Exporter</code> instance by calling
 * <code>getExporter</code> with the name of the exporter.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.export.Exporter
 */
public final class Exporters {
    private static final Exporters EXPORTERS = new Exporters();

    /**
     * Get an instance of this class.
     * 
     * @return An instance of this class.
     */
    public static Exporters getExporters() {
        return EXPORTERS;
    }

    private Map<String, Exporter> exporters;

    private Exporters() {
        this.exporters = new HashMap<>();
        
        try {
            registerExporter(JSONExporter.class);
            registerExporter(RTFExporter.class);
            registerExporter(NotionExporter.class);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register exporters", e);
        }
    }

    /**
     * Register an exporter by class.
     * 
     * @param exporterClass The exporter class. Must have the
     *                      <code>ExporterInfo</code> annotation.
     * @throws IllegalArgumentException If the exporter class is missing
     *                                  the <code>ExporterInfo</code>
     *                                  annotation or if an exporter is
     *                                  already registered with the name
     *                                  specified in the annotation.
     * @throws NoSuchMethodException If the exporter class does not
     *                               have a no-arg constructor.
     * @throws SecurityException If a security manager is present and
     *                           it denies access to the constructor.
     */
    public void registerExporter(Class<? extends Exporter> exporterClass) throws IllegalArgumentException, NoSuchMethodException, SecurityException {
        ExporterInfo info = exporterClass.getAnnotation(ExporterInfo.class);
        if (info == null)
            throw new IllegalArgumentException("Exporter class missing ExporterInfo annotation: " + exporterClass.getName());
        
        Constructor<? extends Exporter> constructor = exporterClass.getConstructor();
        Exporter exporter;
        try {
            exporter = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to instantiate exporter: " + exporterClass.getName(), e);
        }

        registerExporter(info.name(), exporter);
    }

    /**
     * Register an exporter by name.
     * 
     * @param name The name of the exporter.
     * @param exporter The exporter.
     * @throws IllegalArgumentException If an exporter is already
     *                                  registered with the given name.
     */
    public void registerExporter(String name, Exporter exporter) throws IllegalArgumentException {
        exporters.compute(name.toLowerCase(), (k, v) -> {
            if (v != null)
                throw new IllegalArgumentException("Exporter already registered: " + name);
            return exporter;
        });
    }

    /**
     * Retrieve an exporter by name.
     * 
     * @param name The name of the exporter.
     * @return The requested exporter or <code>null</code> if no
     *         exporter is registered with the given name.
     */
    public Exporter getExporter(String name) {
        return exporters.get(name.toLowerCase());
    }
}

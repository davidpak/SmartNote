package com.smartnote.server.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation for server routes. Describes the method type and path
 * that the route will be bound to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerRoute {
    /**
     * The method type to bind to.
     * 
     * @return The method type.
     */
    public MethodType method();

    /**
     * The path to bind to.
     * 
     * @return The path.
     */
    public String path();
}

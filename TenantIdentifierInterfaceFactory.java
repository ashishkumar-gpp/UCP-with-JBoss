package com.finastra.jboss.module;

import java.lang.reflect.InvocationTargetException;

public class TenantIdentifierInterfaceFactory {
    public static TenantIdentifierInterface createClass(String className) {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = Class.forName(className, true, contextClassLoader);
            if (TenantIdentifierInterface.class.isAssignableFrom(clazz)) {
                return (TenantIdentifierInterface) clazz.getDeclaredConstructor().newInstance();
            } else {
                throw new IllegalArgumentException("Class " + className + " does not implement TenantIdentifierInterface");
            }
        } catch (ClassNotFoundException e) {
            return new DefaultSystemTenant();
            // throw new RuntimeException("Class " + className + " not found", e);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException("Failed to create an instance of class: " + className, e);
        }
    }
}

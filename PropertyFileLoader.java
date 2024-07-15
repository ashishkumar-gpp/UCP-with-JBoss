package com.finastra.jboss.module;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyFileLoader {
    private static PropertyFileLoader instance;
    private ConcurrentHashMap<String, String> propertiesMap;

    private PropertyFileLoader() {
        propertiesMap = new ConcurrentHashMap<>();
        loadProperties();
    }

    public static PropertyFileLoader getInstance() {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null)
                    instance = new PropertyFileLoader();
            }
        }
        return instance;
    }

    private synchronized void loadProperties() {
        String filename = System.getProperty("finastra.tenant.config");

        if (filename != null) {
            try (InputStream input = new FileInputStream(filename)) {
                Properties properties = new Properties();
                properties.load(input);
                for (String name : properties.stringPropertyNames()) {
                    propertiesMap.put(name, properties.getProperty(name));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getProperty(String key) {
        String value = propertiesMap.get(key);
        if (value == null) {
            synchronized (propertiesMap) {
                value = propertiesMap.get(key);
                if (null == value ) {
                    loadProperties();
                    value = propertiesMap.get(key);
                }
            }
        }

        return value;
    }
}
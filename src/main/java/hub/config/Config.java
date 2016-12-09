package de.mobile.inventorylistindexer.config;

import java.util.Optional;

import javax.annotation.Nullable;

public final class Config {

    private Config() {
    }

    public static String mustExist(String key) {
        final String value = resolve(key);
        if (value == null) {
            throw new RuntimeException("Configuration key '" + key + "' not found!");
        }

        return value;
    }

    public static Optional<String> get(String key) {
        return Optional.ofNullable(resolve(key));
    }

    public static String get(String key, String def) {
        return get(key).orElse(def);
    }

    public static Boolean get(String key, Boolean def) {
        return get(key).map(Boolean::valueOf).orElse(def);
    }

    public static Integer get(String key, Integer def) {
        return Integer.valueOf(get(key, def.toString()));
    }

    public static Long get(String key, Long def) {
        return Long.valueOf(get(key, def.toString()));
    }

    public static Integer getInt(String key) {
        return Integer.valueOf(mustExist(key));
    }

    public static boolean isProd() {
        return "prod".equals(mustExist("environment"));
    }

    public static boolean isDev() {
        return "dev".equals(mustExist("environment"));
    }

    @Nullable
    private static String resolve(String key) {
        final String property = System.getProperty(key);
        if (property != null) {
            return property;
        }

        final String env = System.getenv(key);
        if (env != null) {
            return env;
        }

        return null;
    }

}

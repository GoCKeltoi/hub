package de.mobile.inventorylistindexer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheckRegistry;

import de.mobile.inventorylistindexer.config.Config;

public class Main {

    public static void main(String[] args) throws Exception {
        configureLog4j();
        logStartupInfo();

        final IndexerComponent app = DaggerIndexerComponent.create();

        new Thread(() -> logHealthStatus(app.healthCheckRegistry())).start();

        // start tomcat
        app.webserver().start();

        final Thread continuous = new Thread(() -> app.continuousIndexer().start(), "realtime-indexer");
        continuous.start();

        if (!Config.isProd()) {
            app.fullIndexBuilder().fullIndex();
        }

        continuous.join();
    }

    private static void logHealthStatus(HealthCheckRegistry health) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        health.runHealthChecks().forEach((name, r) -> {
            if (!r.isHealthy()) {
                logger.error("{} is not healthy: {}", name, r.getMessage());
            }
        });
    }

    private static void logStartupInfo() {
        System.out.println("JVM: " + System.getProperty("java.version"));
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        System.out.println("System properties: " + System.getProperties());
        System.out.println("System environment: " + System.getenv());

        // this must be initialized here and not as a static field in this class
        // because we want to ensure we get a chance to configure log4j
        // before it initializes itself
        Logger logger = LoggerFactory.getLogger(Main.class);

        logger.info("JVM: {}", System.getProperty("java.version"));
        logger.info("Classpath: {}", System.getProperty("java.class.path"));
        logger.info("System properties : {}", System.getProperties());
        logger.info("System environment: {}", System.getenv());
    }

    private static void configureLog4j() {
        System.setProperty("log4j.debug", "true");

        Config.get("environment")
                .map(env -> "log4j-" + env + ".properties")
                .ifPresent(fileName -> {
                    System.setProperty("log4j.configuration", fileName);
                    System.out.println("Using " + fileName + " as log4j configuration file");
                });
    }

}


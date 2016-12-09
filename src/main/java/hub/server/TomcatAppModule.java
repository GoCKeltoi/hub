package de.mobile.inventorylistindexer.server;

import java.io.File;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.inject.Singleton;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.io.Files;

import dagger.Module;
import dagger.Provides;

import de.mobile.inventorylistindexer.App;
import de.mobile.inventorylistindexer.Route;
import de.mobile.inventorylistindexer.config.Config;


@Module
public class TomcatAppModule {

    @Provides
    @Singleton
    Tomcat tomcat() {
        // ensure tomcat logs using slf4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final Tomcat t = new Tomcat();
        t.setPort(Config.getInt("port"));

        final BiConsumer<String, String> tomcatConfigurer = attrSetter(t, "inventoryList.tomcat");
        tomcatConfigurer.accept("compression", "off");
        tomcatConfigurer.accept("maxConnections", "500");
        tomcatConfigurer.accept("maxThreads", "500");
        tomcatConfigurer.accept("processorCache", "500");

        return t;
    }

    @Provides
    @Singleton
    Context ctx(Tomcat t) {
        final File tempDir = Files.createTempDir();
        tempDir.deleteOnExit();
        return t.addContext("", tempDir.getAbsolutePath());
    }

    private BiConsumer<String, String> attrSetter(Tomcat t, String prefix) {
        String prefixWithDot = prefix.endsWith(".") ? prefix : prefix + ".";
        return (String key, String def) ->
                t.getConnector().setAttribute(prefixWithDot + key, Config.get(prefixWithDot + key, def));
    }

    @Provides
    App assemble(
            Tomcat t,
            Context ctx,
            Set<Route> routes
    ) {
        routes.forEach(r -> {
            final String name = r.servlet.getClass().getName();
            t.addServlet("", name, r.servlet);
            ctx.addServletMapping(r.endpoint, name);
        });

        return new TomcatApp(t);
    }

}

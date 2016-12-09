package de.mobile.inventorylistindexer.monitoring;

import java.util.Set;

import javax.inject.Singleton;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.servlets.HealthCheckServlet;

import dagger.Module;
import dagger.Provides;

import de.mobile.inventorylistindexer.Route;
import de.mobile.inventorylistindexer.util.NamedHealthCheck;

@Module
public class HealthModule {

    @Provides(type = Provides.Type.SET)
    public Route servlet(HealthCheckRegistry registry) {
        return new Route("/internal/health", new HealthCheckServlet(registry));
    }

    @Provides
    @Singleton
    public HealthCheckRegistry registry(Set<NamedHealthCheck> checks) {
        HealthCheckRegistry registry = new HealthCheckRegistry();
        checks.forEach(c -> registry.register(c.name, c));
        return registry;
    }

    // used mainly to allow building the webapp with no healthchecks
    @Provides(type = Provides.Type.SET)
    public NamedHealthCheck deadlockDetector() {
        return NamedHealthCheck.of("DeadlockDetector", new ThreadDeadlockHealthCheck());
    }
}

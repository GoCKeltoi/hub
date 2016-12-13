package hub.monitoring;

import javax.inject.Singleton;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

import dagger.Module;
import dagger.Provides;

import hub.Route;

@Module
@Singleton
public class MetricsModule {

    @Provides(type = Provides.Type.SET)
    public Route metrics(MetricRegistry mr) {
        return new Route("/metrics", new MetricsServlet(mr));
    }

    @Provides @Singleton
    public MetricRegistry mr() {
        final MetricRegistry mr = new MetricRegistry();
        JvmMetrics.register(mr);
        GraphiteReporting.register(mr);
        return mr;
    }

}

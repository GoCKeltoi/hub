package hub;

import com.codahale.metrics.health.HealthCheckRegistry;
import dagger.Component;
import hub.elasticsearch.ElasticSearchModule;
import hub.indexer.ContinuousIndexer;
import hub.indexer.FullIndexBuilder;
import hub.indexer.IndexerModule;
import hub.kafka.KafkaModule;
import hub.monitoring.HealthModule;
import hub.monitoring.MetricsModule;
import hub.server.TomcatAppModule;
import hub.web.ServletsModule;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
        TomcatAppModule.class,
        ServletsModule.class,
        MetricsModule.class,
        HealthModule.class,
        IndexerModule.class,
        ElasticSearchModule.class,
        KafkaModule.class
})
interface HubComponent {
    App webserver();

    HealthCheckRegistry healthCheckRegistry();

    FullIndexBuilder fullIndexBuilder();
    ContinuousIndexer continuousIndexer();

}

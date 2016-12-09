package de.mobile.inventorylistindexer;

import javax.inject.Singleton;

import com.codahale.metrics.health.HealthCheckRegistry;

import dagger.Component;

import de.mobile.inventorylistindexer.client.InventoryApiModule;
import de.mobile.inventorylistindexer.client.OrderApiModule;
import de.mobile.inventorylistindexer.elasticsearch.ElasticSearchModule;
import de.mobile.inventorylistindexer.indexer.ContinuousIndexer;
import de.mobile.inventorylistindexer.indexer.FullIndexBuilder;
import de.mobile.inventorylistindexer.indexer.IndexerModule;
import de.mobile.inventorylistindexer.kafka.KafkaModule;
import de.mobile.inventorylistindexer.monitoring.HealthModule;
import de.mobile.inventorylistindexer.monitoring.MetricsModule;
import de.mobile.inventorylistindexer.server.TomcatAppModule;
import de.mobile.inventorylistindexer.web.ServletsModule;

@Singleton
@Component(
        modules = {
        TomcatAppModule.class,
        ServletsModule.class,
        MetricsModule.class,
        HealthModule.class,
        IndexerModule.class,
        OrderApiModule.class,
        InventoryApiModule.class,
        ElasticSearchModule.class,
        KafkaModule.class
})
interface IndexerComponent {
    App webserver();

    HealthCheckRegistry healthCheckRegistry();

    FullIndexBuilder fullIndexBuilder();
    ContinuousIndexer continuousIndexer();

}

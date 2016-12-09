package de.mobile.inventorylistindexer.indexer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

import de.mobile.inventorylistindexer.client.InventoryApi;
import de.mobile.inventorylistindexer.client.OrderApi;
import de.mobile.inventorylistindexer.elasticsearch.DocumentIndexer;
import de.mobile.inventorylistindexer.elasticsearch.DocumentIndexerImpl;
import de.mobile.inventorylistindexer.elasticsearch.EsAliasResolver;
import de.mobile.inventorylistindexer.kafka.TopicConnectionFactory;
import de.mobile.inventorylistindexer.util.MonitoringProxy;


@Module
public class IndexerModule {

    @Provides
    @Singleton
    VehicleEventConsumer provideVehicleEventConsumer(
            @Named("vehicleIndexer") DocumentIndexer<VehicleESDoc> docIndexer,
            OrderApi orderApi,
            InventoryApi inventoryApi,
            Gson gson,
            MetricRegistry mr
    ) {
        return new VehicleEventConsumer(docIndexer, orderApi, inventoryApi, gson, mr);
    }

    @Provides
    @Singleton
    @Named("vehicleIndexer")
    DocumentIndexer<VehicleESDoc> provideVehicleEsIndexer(
            Client esclient,
            Gson gson,
            MetricRegistry mr
    ) {
        return MonitoringProxy.<DocumentIndexerImpl>builder()
                .metricRegistry(mr)
                .namespace("outbound.ElasticSearch")
                .clazz(DocumentIndexerImpl.class)
                .src(new DocumentIndexerImpl("vehicle", esclient, gson))
                .build();
    }

    @Provides
    @Singleton
    FullIndexBuilder provideFullIndexBuilder(
            EsAliasResolver aliasResolver,
            Client client,
            TopicConnectionFactory tcf,
            VehicleEventConsumer vec,
            MetricRegistry mr
    ) {
        return new FullIndexBuilderImpl(aliasResolver, client, tcf, vec, mr);
    }

    @Provides
    @Singleton
    ContinuousIndexer provideContinuousIndexer(
            EsAliasResolver aliasResolver,
            TopicConnectionFactory tcf,
            VehicleEventConsumer vec,
            MetricRegistry mr
    ) {
        return new ContinuousIndexer(aliasResolver, tcf, vec, mr);
    }



}

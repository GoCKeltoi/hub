package hub.indexer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

import hub.client.InventoryApi;
import hub.client.OrderApi;
import hub.elasticsearch.DocumentIndexer;
import hub.elasticsearch.DocumentIndexerImpl;
import hub.elasticsearch.EsAliasResolver;
import hub.kafka.TopicConnectionFactory;
import hub.util.MonitoringProxy;


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

package hub.indexer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

import hub.elasticsearch.DocumentIndexer;
import hub.elasticsearch.DocumentIndexerImpl;
import hub.elasticsearch.EsAliasResolver;
import hub.kafka.TopicConnectionFactory;
import hub.util.MonitoringProxy;

import java.util.Map;


@Module
public class IndexerModule {

    @Provides
    @Singleton
    VehicleEventConsumer provideVehicleEventConsumer(
            @Named("vehicleIndexer") DocumentIndexer<Map<String, Object>> docIndexer,
            Gson gson,
            MetricRegistry mr
    ) {
        return new VehicleEventConsumer(docIndexer, gson, mr);
    }

    @Provides
    @Singleton
    @Named("vehicleIndexer")
    DocumentIndexer<Map<String, Object>> provideVehicleEsIndexer(
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
        return new FullIndexBuilderImpl(aliasResolver, client, tcf, mr);
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

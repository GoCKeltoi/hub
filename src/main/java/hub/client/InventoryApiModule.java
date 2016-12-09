package de.mobile.inventorylistindexer.client;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import com.codahale.metrics.MetricRegistry;

import dagger.Module;
import dagger.Provides;

import de.mobile.inventorylistindexer.util.MoreOptional;
import de.mobile.inventorylistindexer.util.NamedHealthCheck;

@Module
public class InventoryApiModule {

    @Provides(type = Provides.Type.SET_VALUES)
    @Singleton
    Set<NamedHealthCheck> provideHealth(ServiceClient<InventoryApi> sc) {
        return MoreOptional.toSet(sc.healthCheck);
    }

    @Provides
    @Singleton
    InventoryApi provideInventoryApi(ServiceClient<InventoryApi> sc) {
        return sc.api;
    }

    @Provides
    @Singleton
    ServiceClient<InventoryApi> provideInventoryApiServiceClient(
            MetricRegistry mr
    ) {
        return ServiceClient
                .builder()
                .withBaseUrlFromConfig("inventoryServiceEndpoint")
                .withPingResource("internal/ping")
                .withMetrics(mr)
                .withTimeout(10, TimeUnit.SECONDS)
                .build(InventoryApi.class);
    }

}

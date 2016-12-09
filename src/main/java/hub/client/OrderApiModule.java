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
public class OrderApiModule {

    @Provides(type = Provides.Type.SET_VALUES)
    @Singleton
    Set<NamedHealthCheck> provideHealth(ServiceClient<OrderApi> sc) {
        return MoreOptional.toSet(sc.healthCheck);
    }

    @Provides
    @Singleton
    OrderApi provideOrderApi(ServiceClient<OrderApi> sc) {
        return sc.api;
    }

    @Provides
    @Singleton
    ServiceClient<OrderApi> provideOrderApiServiceClient(MetricRegistry mr) {
        return ServiceClient
                .builder()
                .withBaseUrlFromConfig("orderServiceEndpoint")
                .withPingResource("internal/ping")
                .withMetrics(mr)
                .withTimeout(10, TimeUnit.SECONDS)
                .build(OrderApi.class);
    }

}

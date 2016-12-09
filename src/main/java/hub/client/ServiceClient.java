package de.mobile.inventorylistindexer.client;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import de.mobile.inventorylistindexer.config.Config;
import de.mobile.inventorylistindexer.util.NamedHealthCheck;
import de.mobile.inventorylistindexer.util.ServiceHealthCheck;
import de.mobile.inventorylistindexer.util.http.OkHttpPerformanceMeter;
import de.mobile.inventorylistindexer.util.http.RequestResponseLogger;

class ServiceClient<T> {

    public final T api;
    public final Optional<NamedHealthCheck> healthCheck;

    ServiceClient (T api, Optional<NamedHealthCheck> healthCheck) {
        this.api = api;
        this.healthCheck = healthCheck;
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String name;
        private MetricRegistry mr;
        private String baseUrl;
        private String pingResource;
        private long timeout;
        private TimeUnit unit;

        Builder withMetrics(MetricRegistry mr) {
            this.mr = requireNonNull(mr);
            return this;
        }

        Builder withBaseUrlFromConfig(String baseUrl) {
            this.baseUrl = Config.mustExist(baseUrl);
            return this;
        }

        Builder withPingResource(String pingResource) {
            this.pingResource = requireNonNull(pingResource);
            return this;
        }

        Builder withTimeout(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        <T> ServiceClient<T> build(Class<T> retrofitInterface) {
            this.name = requireNonNull(retrofitInterface).getSimpleName();
            OkHttpClient client = httpClient(name);

            return new ServiceClient<>(
                    apiClient(retrofitInterface, client),
                    healthCheck(client)
            );
        }

        private OkHttpClient httpClient(String metricName) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.connectTimeout(1, TimeUnit.SECONDS);
            if (unit != null) {
                builder.readTimeout(timeout, unit);
                builder.writeTimeout(timeout, unit);
            }

            if (mr != null) {
                builder.addInterceptor(new OkHttpPerformanceMeter(mr, "outbound." + metricName));
            }

            if (!Config.isProd()) {
                builder.addInterceptor(new RequestResponseLogger(metricName));
            }

            return builder.build();
        }

        private <T> T apiClient(Class<T> retrofitInterface, OkHttpClient client) {
            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .validateEagerly(true)
                    .client(client)
                    .build()
                    .create(retrofitInterface);
        }

        private Optional<NamedHealthCheck> healthCheck(OkHttpClient client) {
            return Optional
                    .ofNullable(pingResource)
                    .map(r -> new ServiceHealthCheck(name, baseUrl + pingResource, client));
        }


    }

}

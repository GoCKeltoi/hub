package de.mobile.inventorylistindexer.elasticsearch;


import javax.inject.Singleton;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;

import dagger.Module;
import dagger.Provides;

import de.mobile.inventorylistindexer.config.Config;
import de.mobile.inventorylistindexer.util.NamedHealthCheck;


@Module
public class ElasticSearchModule {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchModule.class);

    @Provides
    @Singleton
    public Client provideEsClient() {
        final HostAndPort hostAndPort = HostAndPort
                .fromString(Config.mustExist("inventoryEsSearchEndpoint"))
                .withDefaultPort(9300);

        logger.info("Configuring ES connection to: {}", hostAndPort);

        final Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                // sniff for new instances only in prod, where we have a cluster
                // otherwise, disable to avoid connectivity issues during local development
                .put("client.transport.sniff", Config.isProd())
                .build();

        final TransportClient tc = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(hostAndPort.getHostText(), hostAndPort.getPort()));

        Runtime.getRuntime().addShutdownHook(new Thread(tc::close));

        return tc;
    }

    @Provides(type = Provides.Type.SET)
    public NamedHealthCheck provideEsHealthCheck(Client client) {
        return NamedHealthCheck.of("ElasticSearch", new ESHealthCheck(client));
    }

}

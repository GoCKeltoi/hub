package de.mobile.inventorylistservice.elasticsearch;


import com.google.common.net.HostAndPort;
import dagger.Module;
import dagger.Provides;
import de.mobile.inventorylistservice.config.Config;
import de.mobile.inventorylistservice.util.NamedHealthCheck;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.inject.Singleton;



@Module
public class ElasticSearchModule {
    @Provides
    @Singleton
    public Client elasticSearchClient(Config config) {
        final HostAndPort hostAndPort = HostAndPort
                .fromString(config.get("inventoryEsSearchEndpoint", "essearchlow.mobile.rz"))
                .withDefaultPort(9300);

        final Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("client.transport.sniff", true)
                .build();

        return new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(hostAndPort.getHostText(), hostAndPort.getPort()));

    }

    @Provides(type = Provides.Type.SET)
    public NamedHealthCheck elasticSearchHealthCheck(Client client) {
        return NamedHealthCheck.of("ElasticSearch", new ESHealthCheck(client));
    }





}

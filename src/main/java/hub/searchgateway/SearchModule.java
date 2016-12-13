package hub.searchgateway;

import dagger.Module;
import dagger.Provides;
import org.elasticsearch.client.Client;

import javax.inject.Singleton;

@Module
public class SearchModule {

    @Provides
    @Singleton
    public SearchGateway sg(Client client) {
        return new ElasticSearchGateway(client);
    }

}

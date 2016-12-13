package hub.elasticsearch;

import java.util.Optional;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class EsAliasResolver implements Supplier<Optional<String>> {

    private static final Logger logger = LoggerFactory.getLogger(EsAliasResolver.class);

    public static final String ES_INDEX_NAME = "inventory-list-vehicles-v2";

    private final Client esclient;

    @Inject
    public EsAliasResolver(Client esclient) {
        this.esclient = esclient;
    }

    @Override
    public Optional<String> get() {
        try {
            GetAliasesResponse response = esclient.admin()
                    .indices()
                    .getAliases(new GetAliasesRequest(ES_INDEX_NAME))
                    .actionGet();

            if (!response.getAliases().isEmpty()) {
                String indexName = response.getAliases().iterator().next().key;
                return Optional.of(indexName);
            }
        } catch (ElasticsearchException ese) {
            logger.warn("Failed to query ES: {}", ese.getMessage());
        }
        return Optional.empty();
    }

}

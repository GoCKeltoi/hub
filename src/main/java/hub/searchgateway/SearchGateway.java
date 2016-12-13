package hub.searchgateway;

import java.util.concurrent.CompletableFuture;

public interface SearchGateway {
    CompletableFuture<org.elasticsearch.action.search.SearchResponse> filterOptionsBy(SearchRequest searchRequest);

    CompletableFuture<SearchResponse> findBy(SearchRequest searchRequest);
}

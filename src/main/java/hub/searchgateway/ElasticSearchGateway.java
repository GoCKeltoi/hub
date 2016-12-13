package hub.searchgateway;

import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.FilterBuilders.regexpFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import de.mobile.async.converter.ListenableActionCompletableFuture;

import com.google.common.base.Joiner;
import de.mobile.elastic.util.Tokenizers;


import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.index.query.FilterBuilders.queryFilter;
import static org.elasticsearch.index.query.QueryBuilders.simpleQueryStringQuery;


@SuppressWarnings("PMD")
public final class ElasticSearchGateway implements SearchGateway {

    // TODO: think about how to put it in one place
    private static final String INDEX_NAME = "inventory-list-vehicles-v2";

    public static final String INDEX_TYPE = "v1";
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchGateway.class);

    private final Client client;

    public ElasticSearchGateway(Client client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<org.elasticsearch.action.search.SearchResponse> filterOptionsBy(SearchRequest searchRequest) {
        return ElasticSearchRequest.from(searchRequest)
                .withClient(client)
                .filterOptionsAsync();
    }

    @Override
    public CompletableFuture<SearchResponse> findBy(SearchRequest searchRequest) {
        final CompletableFuture<SearchResponse> csr = ElasticSearchRequest.from(searchRequest)
            .withClient(client)
            .searchAsync();
        return csr;
    }

    private static class ElasticSearchRequest {

        private final SearchRequest searchRequest;
        private Client client;
        private FilteredQueryBuilder filteredQueryBuilder;
        private SearchRequestBuilder searchRequestBuilder;

        private ElasticSearchRequest(SearchRequest searchRequest) {
            this.searchRequest = searchRequest;
        }

        public static ElasticSearchRequest from(SearchRequest sr) {
            return new ElasticSearchRequest(sr);
        }

        public ElasticSearchRequest withClient(Client client) {
            this.client = client;
            return this;
        }

        public CompletableFuture<SearchResponse> searchAsync() {
            withFilterBuilder();
            withSearchRequestBuilder();
            withPagination();
            withSort();
            return search(convert());
        }

        public CompletableFuture<org.elasticsearch.action.search.SearchResponse> filterOptionsAsync() {
            withFilterBuilder();
            withSearchRequestBuilder();
            withFilterOptions();
            withoutHits();
            return search(r -> r);
        }

        private void withFilterBuilder() {
            final FilterBuilder filter = ConvertibleElasticFilter.from(searchRequest).convert();
            filteredQueryBuilder = filteredQuery(matchAllQuery(), filter);
        }

        private void withSearchRequestBuilder() {
            searchRequestBuilder = client.prepareSearch(INDEX_NAME);
            searchRequestBuilder.setQuery(filteredQueryBuilder);
        }

        private void withPagination() {
            searchRequestBuilder.setFrom((searchRequest.page().number()-1) * searchRequest.page().size());
            searchRequestBuilder.setSize(searchRequest.page().size());
        }

        private void withSort() {
            if (!searchRequest.sort().isPresent()) withDefaultSort();
            else withSortFromRequest();
        }

        private void withDefaultSort() {
            searchRequestBuilder.addSort(sortBuilder("vehicleId",  SortOrder.ASC));
        }

        private void withSortFromRequest() {
            final SearchRequest.Sort sort = searchRequest.sort().get();
            final SortOrder sortOrder = SortOrder.valueOf(sort.order().name());

            for (String sortField:sort.fields()) {
                searchRequestBuilder.addSort(sortBuilder(sortField, sortOrder));
            }
        }

        private void withFilterOptions() {
            TermsBuilder models = createAggregation("models", "modelId", Optional.empty());
            TermsBuilder makes = createAggregation("makes", "makeId", Optional.of(models));
            TermsBuilder categories = createAggregation("vehicleCategories", "vehicleCategory", Optional.of(makes));
            searchRequestBuilder.addAggregation(categories);
        }

        private void withoutHits() {
            searchRequestBuilder.setSize(0);
        }

        private TermsBuilder createAggregation(String name, String field, Optional<TermsBuilder> subAggs) {
            TermsBuilder aggregation = AggregationBuilders.terms(name).field(field).size(0);
            return subAggs.map(aggregation::subAggregation).orElse(aggregation);
        }

        static SortBuilder sortBuilder(String field, SortOrder order){
            return SortBuilders
                    .fieldSort(field)
                    .order(order);
        }

        private <T> CompletableFuture<T> search(Function<org.elasticsearch.action.search.SearchResponse, T> convert) {
            final CompletableFuture<org.elasticsearch.action.search.SearchResponse> response;
            response = new CompletableFuture<>();
            try {
                ListenableActionCompletableFuture.from(searchRequestBuilder.execute())
                        .thenAccept(response::complete)
                        .exceptionally(t -> {
                            response.completeExceptionally(t);
                            return null;
                        });
            } catch (Exception e) {
                response.completeExceptionally(e);
            }
            return response.thenApply(convert);
        }

        private Function<org.elasticsearch.action.search.SearchResponse, SearchResponse> convert() {
            return elasticSearchResponse -> {
                logger.debug("Found filter options:\n{}", elasticSearchResponse);
                SearchResponse sr = ConvertibleSearchResponse.from(elasticSearchResponse).convert();
                return sr;
            };
        }
    }

    private static class ConvertibleElasticFilter {

        private static final String LETTERS = "[^\\p{L}\\w]";
        private final SearchRequest searchRequest;
        private AndFilterBuilder filterBuilder;

        private ConvertibleElasticFilter(SearchRequest searchRequest) {
            this.searchRequest = searchRequest;
        }

        public static ConvertibleElasticFilter from(SearchRequest searchRequest) {
            return new ConvertibleElasticFilter(searchRequest);
        }

        public FilterBuilder convert() {
            filterBuilder = andFilter();

            processCustomerId();
            processVehicleId();
            processVehicleCategory();
            processCategory();
            processCondition();
            processMakeId();
            processModelId();
            processModelVariant();
            processInternalNumber();
            processUploadSticky();
            processUsageType();
            processReserved();
            return filterBuilder;
        }

        private void processModelVariant() {
            searchRequest.modelDescription().ifPresent(
                    modelDescription -> filterBuilder.add(queryFilter( queryTokenizedText("modelDescriptionSearch", modelDescription)))
            );
        }

        static QueryBuilder queryTokenizedText(String field, String text) {
            return simpleQueryStringQuery(tokenize(text))
                    .field(field)
                    .defaultOperator(SimpleQueryStringBuilder.Operator.AND);
        }

        private static final Joiner TOKEN_JOINER = Joiner.on(' ').skipNulls();

        static String tokenize(String text) {
            return TOKEN_JOINER.join(Tokenizers.searchTokenizer.tokenize(text));
        }

        private void processUsageType() {
            searchRequest.usageType().ifPresent(
                    usageType -> filterBuilder.add(termFilter("usageType", usageType))
            );
        }

        private void processReserved() {
            searchRequest.reserved().ifPresent(
                    reserved -> filterBuilder.add(termFilter("reserved", reserved))
            );
        }

        private void processUploadSticky() {
            searchRequest.uploadSticky().ifPresent(
                    uploadSticky -> filterBuilder.add(termFilter("uploadSticky", uploadSticky))
            );
        }

        private void processVehicleId() {
            searchRequest.vehicleId().ifPresent(
                    vehicleId -> filterBuilder.add(termFilter("vehicleId", vehicleId))
            );
        }

        private void processCustomerId() {
            searchRequest.customerId().ifPresent(
                    customerId -> filterBuilder.add(termFilter("customerId", customerId))
            );
        }

        private void processCategory() {
            searchRequest.category().ifPresent(
                    category -> filterBuilder.add(termFilter("category", category))
            );
        }

        private void processVehicleCategory() {
            searchRequest.vehicleCategory().ifPresent(
                    vehicleCategory -> filterBuilder.add(termFilter("vehicleCategory", vehicleCategory))
            );
        }

        private void processCondition() {
            searchRequest.condition().ifPresent(
                    condition -> filterBuilder.add(termFilter("condition", condition))
            );
        }

        private void processMakeId() {
            searchRequest.makeId().ifPresent(
                    makeId -> filterBuilder.add(termFilter("makeId", makeId))
            );
        }

        private void processModelId() {
            searchRequest.modelId().ifPresent(
                    modelId -> filterBuilder.add(termFilter("modelId", modelId))
            );
        }


        private void processInternalNumber() {
            searchRequest.internalNumber().ifPresent(
                    internalNumber -> {
                        String pattern = ".*" + internalNumber.trim().toLowerCase().replaceAll(LETTERS, ".*") + ".*";
                        logger.info("pattern " + pattern );
                        filterBuilder.add(regexpFilter("internalNumber", pattern));
                    }

            );
        }
    }
}

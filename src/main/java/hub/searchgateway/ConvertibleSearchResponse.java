package hub.searchgateway;

import com.google.gson.Gson;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class ConvertibleSearchResponse {

    private final org.elasticsearch.action.search.SearchResponse eSResponse;
    private SearchResponse searchResponse;

    private ConvertibleSearchResponse(org.elasticsearch.action.search.SearchResponse esr) {
        this.eSResponse = esr;
    }

    public static ConvertibleSearchResponse from(org.elasticsearch.action.search.SearchResponse esr) {
        return new ConvertibleSearchResponse(esr);
    }

    public SearchResponse convert() {
        searchResponse = new SearchResponse();
        withVehicles();
        withTookInMillis();
        withTotalResults();
        return searchResponse;
    }

    private void withVehicles() {
        final List<VehicleESDoc> vehicles = Arrays.stream(eSResponse.getHits().getHits())
            .map(searchHit -> ConvertibleElasticSearchVehicle.from(searchHit).convert())
            .collect(Collectors.toList());
        searchResponse.withVehicles(vehicles);
    }

    private void withTookInMillis() {
        searchResponse.withTookInMillis(eSResponse.getTookInMillis());
    }

    private void withTotalResults() {
        searchResponse.withTotalHitsCount(eSResponse.getHits().totalHits());
    }

    private static class ConvertibleElasticSearchVehicle {

        private static final Gson GSON = new Gson();
        private final SearchHit searchHit;

        private ConvertibleElasticSearchVehicle(SearchHit searchHit) {
            this.searchHit = searchHit;
        }

        public static ConvertibleElasticSearchVehicle from(SearchHit searchHit) {
            return new ConvertibleElasticSearchVehicle(searchHit);
        }

        public VehicleESDoc convert() {
            return GSON.fromJson(searchHit.sourceAsString(), VehicleESDoc.class);
        }
    }
}

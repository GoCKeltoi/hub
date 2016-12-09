package de.mobile.inventorylistservice.filteroptions;

import de.mobile.inventorylistservice.searchgateway.SearchGateway;
import de.mobile.inventorylistservice.searchgateway.SearchRequest;
import de.mobile.inventorylistservice.translation.Translator;
import de.mobile.inventorylistservice.translation.makemodel.MakeModelResolver;
import de.mobile.inventorylistservice.translation.renderer.RendererUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FilterOptionsServiceImpl implements FilterOptionsService {
    private final Logger logger = LoggerFactory.getLogger(FilterOptionsServiceImpl.class);
    private final SearchGateway gateway;
    private final MakeModelResolver resolver;
    private final Translator translator;
    private final VehicleCategoryComparator byVehicleCategory = new VehicleCategoryComparator();

    public FilterOptionsServiceImpl(SearchGateway gateway, MakeModelResolver resolver, Translator translator) {
        this.gateway = gateway;
        this.resolver = resolver;
        this.translator = translator;
    }

    public FilterOptions options(SearchRequest searchRequest, Locale l) {
        SearchResponse response = waitOnResponse(gateway.filterOptionsBy(searchRequest));
        logger.debug("Found filter options: {}", response);
        Aggregations aggregations = response.getAggregations();
        List<VehicleCategoryOption> categories = convertToVehicleCategories(aggregations, l);

        return new FilterOptions(categories);
    }

    private List<VehicleCategoryOption> convertToVehicleCategories(Aggregations aggregations, Locale l) {
        return aggregations.<Terms>get("vehicleCategories")
                .getBuckets()
                .stream()
                .map(bucket -> new VehicleCategoryOption(bucket.getKey(), RendererUtils.renderCarEnum(translator, bucket.getKey(), l), convertToMakes(bucket.getAggregations(), l)))
                .sorted(byVehicleCategory)
                .collect(Collectors.toList());
    }

    private List<MakeOption> convertToMakes(Aggregations aggregations, Locale l) {
        return aggregations.<Terms>get("makes")
                .getBuckets()
                .stream()
                .map(bucket -> new MakeOption(bucket.getKey(), resolver.resolve(bucket.getKey(), l), convertToModels(bucket.getKey(), bucket.getAggregations(), l)))
                .sorted((o1, o2) -> o1.getTr().compareTo(o2.getTr()))
                .collect(Collectors.toList());
    }

    private List<ModelOption> convertToModels(String makeId, Aggregations aggregations, Locale l) {
        return aggregations.<Terms>get("models")
                .getBuckets()
                .stream()
                .map(bucket -> new ModelOption(bucket.getKey(), resolver.resolve(makeId, bucket.getKey(), l)))
                .sorted((o1, o2) -> o1.getTr().compareTo(o2.getTr()))
                .collect(Collectors.toList());
    }

    private SearchResponse waitOnResponse(CompletableFuture<SearchResponse> futureResponse) {
        try {
            return futureResponse.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

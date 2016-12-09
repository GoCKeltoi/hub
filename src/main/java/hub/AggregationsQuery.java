package de.mobile.inventorylistservice;

import de.mobile.inventorylistservice.config.Config;
import de.mobile.inventorylistservice.elasticsearch.ElasticSearchModule;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AggregationsQuery {
    public static void main(String[] args) {
        ElasticSearchModule module = new ElasticSearchModule();
        Client client = module.elasticSearchClient(new Config(key -> Optional.empty()));
        TermsBuilder models = AggregationBuilders.terms("models").field("modelId").size(0);
        TermsBuilder makes = AggregationBuilders.terms("makes").field("makeId").size(0).subAggregation(models);
        TermsBuilder categories = AggregationBuilders.terms("vehicleCategories").field("vehicleCategory").size(0).subAggregation(makes);
        try {
            System.out.println(client.prepareSearch("inventory-list-vehicles").addAggregation(categories).setSize(0).execute().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

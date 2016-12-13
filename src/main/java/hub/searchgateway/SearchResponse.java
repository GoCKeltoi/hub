package hub.searchgateway;

import org.elasticsearch.common.collect.Lists;

public final class SearchResponse {

    private List<VehicleESDoc> vehicles = Lists.newArrayList();
    private Long totalHitsCount;
    private Long tookInMillis;

    public List<VehicleESDoc> vehicles() {
        return vehicles;
    }

    public Integer hitsCount() {
        return vehicles.size();
    }

    public Long totalHitsCount() {
        return totalHitsCount;
    }

    public Long tookInMillis() {
        return tookInMillis;
    }

    public SearchResponse withVehicles(List<VehicleESDoc> vehicles) {
        this.vehicles = vehicles;
        return this;
    }

    public SearchResponse withTookInMillis(long tookInMillis) {
        this.tookInMillis = tookInMillis;
        return this;
    }

    public SearchResponse withTotalHitsCount(Long totalHitsCount) {
        this.totalHitsCount = totalHitsCount;
        return this;
    }
}

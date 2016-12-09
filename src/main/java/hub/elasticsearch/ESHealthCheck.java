package de.mobile.inventorylistindexer.elasticsearch;

import static org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus.YELLOW;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;

import com.codahale.metrics.health.HealthCheck;


class ESHealthCheck extends HealthCheck {
    private final Client client;

    public ESHealthCheck(Client client) {
        this.client = client;
    }

    @Override
    protected Result check() throws Exception {
        final ClusterHealthResponse response = client.admin()
                .cluster()
                .prepareHealth()
                .setWaitForYellowStatus()
                .execute()
                .actionGet();
        final ClusterHealthStatus expected = YELLOW;
        final ClusterHealthStatus current = response.getStatus();
        if (current.value() <= expected.value()) {
            return Result.healthy("Cluster status is %s (should be at least %s)", current, expected);
        } else {
            return Result.unhealthy("Cluster status is %s but should be %s", current, expected);
        }
    }

}

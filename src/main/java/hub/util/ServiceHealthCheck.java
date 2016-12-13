package hub.util;

import com.codahale.metrics.health.HealthCheck;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceHealthCheck extends NamedHealthCheck {

    private final String pingUrl;
    private final OkHttpClient client;

    public ServiceHealthCheck(String name, String pingUrl, OkHttpClient client) {
        super(name);
        this.pingUrl = pingUrl;
        this.client = client;
    }

    @Override
    protected HealthCheck.Result check() throws Exception {
        Request req = new Request.Builder()
                .url(pingUrl)
                .build();

        Response rsp = null;
        try {
            rsp = client.newCall(req).execute();
            if (rsp.isSuccessful()) {
                return HealthCheck.Result.healthy();
            } else {
                return HealthCheck.Result.unhealthy("Url " + pingUrl + " returned http code: " + rsp.code());
            }
        } catch (Exception ex) {
            return HealthCheck.Result.unhealthy(ex);
        } finally {
            if (rsp != null) {
                rsp.body().close();
            }
        }
    }
}

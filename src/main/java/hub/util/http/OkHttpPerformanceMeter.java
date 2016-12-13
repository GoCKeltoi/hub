package hub.util.http;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp profiler that reports the duration and count of requests grouped by HTTP status code
 *
 * If configured as a network interceptor (recommended), it will be called just before making
 * the network call (cache hits are excluded) and should measure raw response times
 * excluding any request(response) (de-)serialization and any other overhead
 *
 * This class does NOT measure timed-out responses or responses that are invalid according to HTTP protocol.
 * It would not make sense, since there is no HTTP code in this case so the exception is just not caught.
 * Use MonitoringProxy to handle the these special situations.
 *
 * Example:
 * new OkHttpClient().networkInterceptors().add(new OkHttpPerformanceMeter(mr, METRIC_PREFIX));
 */
public class OkHttpPerformanceMeter implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(OkHttpPerformanceMeter.class);

    private final MetricRegistry mr;
    private final String metricName;

    public OkHttpPerformanceMeter(MetricRegistry mr, String metricName) {
        this.mr = requireNonNull(mr);
        this.metricName = metricName;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long elapsed = System.currentTimeMillis() - t1;
        mr.timer(name(metricName, String.valueOf(response.code()))).update(elapsed, MILLISECONDS);

        return response;
    }

}

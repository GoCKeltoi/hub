package de.mobile.inventorylistindexer.util.http;

/**
 * Retrofit profiler that reports the duration and count of requests grouped by HTTP status code
 *
 * This class measures ONLY valid responses
 * (200 or 500 responses are valid, if the content is received in time and can be parsed by retrofit)
 *
 * This profiler is NOT called for network timeouts or response parse errors. It would not make sense anyway,
 * since there is no HTTP code in these cases. Use the retrofit error handler or the MonitoringProxy to measure
 * these situations.
 */
public class RetrofitPerformanceMeter {} /*implements Profiler {

    private static final Logger logger = LoggerFactory.getLogger(RetrofitPerformanceMeter.class);

    private final MetricRegistry mr;
    private final String metric;

    public RetrofitPerformanceMeter(MetricRegistry mr, String metric) {
        this.mr = checkNotNull(mr);
        this.metric = checkNotNull(metric);
    }

    @Override
    public Object beforeCall() {
        return null;
    }

    @Override
    public void afterCall(RequestInformation requestInfo, long elapsedTime, int statusCode, Object beforeCallData) {
        mr.timer(metric + "." + statusCode).update(elapsedTime, MILLISECONDS);
        logger.trace("Retrofit response with status code: {} received in: {}ms", statusCode, elapsedTime);
    }

}
*/
package hub.monitoring;

import java.lang.management.ManagementFactory;
import java.util.Map;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

class JvmMetrics {

    static void register(MetricRegistry mr) {
        registerAll(mr, "jvm.bufferpool", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        registerAll(mr, "jvm.gc", new GarbageCollectorMetricSet());
        registerAll(mr, "jvm.mem", new MemoryUsageGaugeSet());
        registerAll(mr, "jvm.threads", new ThreadStatesGaugeSet());
        registerAll(mr, "jvm.classes", new com.codahale.metrics.jvm.ClassLoadingGaugeSet());
    }

    private static void registerAll(MetricRegistry mr, String prefix, MetricSet metrics) throws IllegalArgumentException {
        for (Map.Entry<String, Metric> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(mr, prefix + "." + entry.getKey(), (MetricSet) entry.getValue());
            } else {
                mr.register(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
    }
}

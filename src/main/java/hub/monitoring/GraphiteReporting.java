package hub.monitoring;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import hub.config.Config;

class GraphiteReporting {

    private static final Logger logger = LoggerFactory.getLogger(GraphiteReporting.class);

    static void register(MetricRegistry mr) {
        final Integer reportingInterval = Config.get("graphiteReportingIntervalSec", 60);

        if (Config.isDev()) {
            ConsoleReporter.forRegistry(mr)
                    .filter((name, metric) -> name.contains("inbound") || name.contains("outbound"))
                    .build()
                    .start(reportingInterval, TimeUnit.SECONDS);
        }

        if (!Config.get("metrics.graphite.host").isPresent()) {
            logger.warn("Graphite logging is disabled");
            return;
        }

        String graphiteHost = Config.mustExist("metrics.graphite.host");
        int graphitePort = Config.getInt("metrics.graphite.port");
        String appPrefix = Config.mustExist("graphitePrefix");

        logger.info("Initializing Graphite reporter to {}:{} with prefix {}", graphiteHost, graphitePort, appPrefix);
        String instanceName = Config.mustExist("HOST") + "_" + Config.mustExist("PORT");
        String hostPrefix = MetricRegistry.name(appPrefix, instanceName);
        Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));

        final GraphiteReporter reporter = GraphiteReporter
                .forRegistry(mr)
                .prefixedWith(hostPrefix)
                .build(graphite);

        reporter.start(reportingInterval, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                reporter.report();
                reporter.stop();
            }
        });
    }

}


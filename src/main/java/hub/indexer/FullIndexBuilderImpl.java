package de.mobile.inventorylistindexer.indexer;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import de.mobile.inventorylistindexer.config.Config;
import de.mobile.inventorylistindexer.elasticsearch.EsAliasResolver;
import de.mobile.inventorylistindexer.indexer.vehicle.Vehicle;
import de.mobile.inventorylistindexer.kafka.TopicConnection;
import de.mobile.inventorylistindexer.kafka.TopicConnectionFactory;
import de.mobile.util.DateUtils;
import de.mobile.util.Pair;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasAction;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static com.google.common.io.Resources.getResource;
import static de.mobile.inventorylistindexer.elasticsearch.EsAliasResolver.ES_INDEX_NAME;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
class FullIndexBuilderImpl implements FullIndexBuilder {
    private static final Logger logger = LoggerFactory.getLogger(FullIndexBuilderImpl.class);

    private static final TimeValue ES_TIMEOUT = TimeValue.timeValueSeconds(10);
    private static final String INDEX_TYPE = "vehicle";
    private static final Gson gson = new Gson();

    private final String esEndpoint;
    private final int batchSize;
    private final DateTimeFormatter timeFormatter;
    private final ObjectReader vehicleReader;
    private final EsAliasResolver aliasResolver;
    private final TopicConnectionFactory tcf;
    private final Client client;
    private final VehicleEventConsumer vec;
    private final Duration catchupDuration;
    private URL inventoryUrl;

    private final MetricRegistry mr;

    FullIndexBuilderImpl(
        EsAliasResolver aliasResolver,
        Client client,
        TopicConnectionFactory tcf,
        VehicleEventConsumer vec,
        MetricRegistry mr
    ) {
        try {
            this.inventoryUrl = new URL(Config.mustExist("inventoryServiceEndpoint") + "vehicles");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        this.vec = vec;
        this.esEndpoint = Config.mustExist("inventoryEsSearchEndpoint");
        this.client = client;
        this.tcf = tcf;
        this.batchSize = Config.get("fullIndexBatchSize", 50_000);
        this.catchupDuration = Duration.ofSeconds(Config.get("fullIndexCatchupDurationSec", 60 * 5));
        this.aliasResolver = aliasResolver;
        this.timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

        ObjectReader defaultReader = new ObjectMapper()
            .reader()
            .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.vehicleReader = defaultReader.withType(Vehicle.class);

        this.mr = mr;
    }

    @Override
    public boolean fullIndex() {
        String indexName = null;
        try {
            indexName = createIndex();

            triggerRefresh(indexName);
            fullIndexInto(indexName);
            triggerRefresh(indexName);

            renameAlias(indexName);

            return true;
        } catch (Exception ex) {
            logger.error("Failed to create full index", ex);
            if (indexName != null) {
                deleteIndex(indexName);
            }
            return false;
        }
    }


    private String createIndex() {
        String ts = timeFormatter.format(LocalDateTime.now(Clock.systemUTC())) + "utc";
        final String indexName = ES_INDEX_NAME + "_" + ts;

        logger.info("Creating new ES index: {}", indexName);
        // create new index
        Settings settings = ImmutableSettings.settingsBuilder()
            //.put("number_of_shards", config.getString("es.index.mobile-ads.number_of_shards").orElse("10"))
            //.put("auto_expand_replicas", config.getString("es.index.mobile-ads.auto_expand_replicas").orElse("1-all"))
            .loadFromClasspath("es-settings.json")
            .build();

        final String mapping;
        try {
            mapping = Resources.toString(getResource("es-schema.json"), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CreateIndexRequest createRequest = new CreateIndexRequest(indexName, settings);
        createRequest.mapping(INDEX_TYPE, mapping);

        CreateIndexResponse response = client.admin().indices().create(createRequest).actionGet(ES_TIMEOUT);
        if (!response.isAcknowledged()) {
            throw new RuntimeException("Could not create new index. ES response is not ACKed.");
        }
        return indexName;
    }

    private void deleteIndex(String index) {
        final DeleteIndexRequest r = new DeleteIndexRequest(index);
        final DeleteIndexResponse rsp = client.admin().indices().delete(r).actionGet(ES_TIMEOUT);
        if (rsp.isAcknowledged()) {
            logger.info("Index {} was deleted", index);
        } else {
            logger.warn("Failed to delete index {}", index);
        }
    }


    private Pair<Integer, Long> prosessJunk(final TopicConnection client, Duration pollTimeout, String indexName) throws IOException {
        int processedRecords = 0;
        long newestTimestamp = 0;
        ConsumerRecords records = client.poll(pollTimeout.toMillis());
        logger.debug("got {} event(s)", records.count());
        if(records.isEmpty()){
            return Pair.create(0, 0L);
        }
        Iterator<ConsumerRecord> ri = records.iterator();
        final HttpURLConnection es = bulkIndexConnection(indexName);
        try (final Writer w = new BufferedWriter(new OutputStreamWriter(es.getOutputStream(), Charsets.UTF_8))) {

            for (; ri.hasNext(); processedRecords++) {
                final ConsumerRecord<String, String> record = (ConsumerRecord) ri.next();
                if (!isDeleteEvent(record)) {
                    if(record.timestamp()>newestTimestamp){
                        newestTimestamp = record.timestamp();
                    }
                    try {
                        final Vehicle vehicle = gson.fromJson(record.value(), Vehicle.class);
                        streamVehicleToEs(w, vehicle);
                    } catch (Exception e){
                        logger.error("record.value() " + record.value());
                    }
                }
            }
            // send request
            w.close();
            try {
                client.commitSync();
            } catch (CommitFailedException e) {
                logger.warn("unable to commit consumer offset", e);
            }
            logger.info("Pushed {} records for bulk indexing into ES", processedRecords);

            boolean error = responseHasErrors(es);
            if (error) {
                throw new RuntimeException("Indexing into ES failed. ES failed to index all documents");
            }
        }
        return Pair.create(processedRecords, newestTimestamp);
    }

    private void fullIndexInto(String indexName) {
        Date fullIndexStartDate = DateUtils.now();
        long fullIndexTargetTimestamp = DateUtils.plusMinutes(fullIndexStartDate, -2).getTime();
        logger.info("Performing full index into: {}", indexName);
        Duration pollTimeout = Duration.ofSeconds(Config.get("pollTimeout", 60));

        try (final TopicConnection client = tcf.fromBeginningClient(indexName)) {

            long total = 0;
            Stopwatch sw = Stopwatch.createStarted();
            boolean execute = true;
            while (execute) {
                Pair<Integer, Long> result = prosessJunk(client, pollTimeout, indexName);
                execute = result.getSecond() < fullIndexTargetTimestamp;
                if(result.getSecond() > 0){
                    logger.info("result.getSecond() " +  result.getSecond());
                    logger.info("diff " + (fullIndexTargetTimestamp - result.getSecond()));
                }

                total += result.getFirst();
                if (sw.elapsed(TimeUnit.SECONDS) > 0) {
                    logger.info("Indexed {} documents in {} sec (speed: {} doc/sec)", total, sw.elapsed(TimeUnit.SECONDS), total / sw.elapsed(TimeUnit.SECONDS));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean responseHasErrors(HttpURLConnection es) throws IOException {
        if(200 != es.getResponseCode()){
            logger.error(es.getResponseMessage());
            return true;
        }
        return false;
    }


    private void streamVehicleToEs(Writer writer, Vehicle v) throws IOException {

        final VehicleESDoc vehicleEs = VehicleEsDocAssembler.buildESDocument(v);

        writer
            .append("{ \"index\" : { \"_id\" : \"")
            .append(vehicleEs.getVehicleId())
            .append("\" } }\n");

        gson.toJson(vehicleEs, writer);
        writer.write("\n");
    }

    private void renameAlias(String indexName) {
        logger.info("Migrating alias to new index: {}", indexName);
        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
        aliasResolver.get().ifPresent(name -> aliasesRequest.removeAlias(name, ES_INDEX_NAME));

        aliasesRequest.addAliasAction(AliasAction.newAddAliasAction(indexName, ES_INDEX_NAME));
        client.admin().indices().aliases(aliasesRequest).actionGet(ES_TIMEOUT);
    }

    private HttpURLConnection bulkIndexConnection(String indexName) throws IOException {
        final String url = String.format("http://%s:9200/%s/%s/_bulk?consistency=quorum",
            esEndpoint,
            indexName,
            INDEX_TYPE);
        logger.info("Creating bulk index connection to ES: {}", url);

        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.connect();
        return conn;
    }

    private void triggerRefresh(String indexName) throws IOException {
        final String url = String.format("http://%s:9200/%s/_refresh",
            esEndpoint,
            indexName);
        logger.info("Refreshing index using url: {}", url);

        final HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.connect();
        conn.getOutputStream().close();
        conn.disconnect();
    }    private static boolean isDeleteEvent(ConsumerRecord<String, String> record) {
        return null == record.value() || record.value().length() <= 0;
    }
}

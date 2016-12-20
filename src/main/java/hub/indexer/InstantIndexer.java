package hub.indexer;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hub.config.Config;
import hub.elasticsearch.EsAliasResolver;
import hub.util.Resolver;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class InstantIndexer {
    private static final Logger logger = LoggerFactory.getLogger(InstantIndexer.class);

    private static final String INDEX_TYPE = "event";

    private static final Gson gson = new Gson();
    private final String esEndpoint;
    private final EsAliasResolver aliasResolver;

    public InstantIndexer(String esEndpoint, EsAliasResolver aliasResolver){
        this.esEndpoint = esEndpoint;
        this.aliasResolver = aliasResolver;
    }

    public void process(List<Map<String,Object>> junk)  throws IOException {
        final String indexName = Resolver.withRetry(aliasResolver, Duration.ofSeconds(Config.get("aliasResolverRetry", 60)));
        final HttpURLConnection es = bulkIndexConnection(indexName);
        try (final Writer w = new BufferedWriter(new OutputStreamWriter(es.getOutputStream(), Charsets.UTF_8))) {

            for (Map<String,Object> map : junk) {
                    try {
                        Type type = new TypeToken<Map<String, Object>>() {}.getType();

                        streamVehicleToEs(w, (String)map.get("id"), map);
                    } catch (Exception e){
                        logger.error("record.value() " + map);
                    }

            }
            // send request
            w.close();
            boolean error = responseHasErrors(es);
            if (error) {
                throw new RuntimeException("Indexing into ES failed. ES failed to index all documents");
            }
        }
    }
    public void processDelete(List<String> junk)  throws IOException {
        final String indexName = Resolver.withRetry(aliasResolver, Duration.ofSeconds(Config.get("aliasResolverRetry", 60)));
        final HttpURLConnection es = bulkIndexConnection(indexName);
        try (final Writer w = new BufferedWriter(new OutputStreamWriter(es.getOutputStream(), Charsets.UTF_8))) {

            for (String key : junk) {
                try {

                    streamVehicleDeleteToEs(w, key);
                } catch (Exception e){
                    logger.error("record.value() " + key);
                }

            }
            // send request
            w.close();
            boolean error = responseHasErrors(es);
            if (error) {
                throw new RuntimeException("Indexing into ES failed. ES failed to index all documents");
            }
        }
    }

    private boolean responseHasErrors(HttpURLConnection es) throws IOException {
        if(200 != es.getResponseCode()){
            logger.error(es.getResponseMessage());
            return true;
        }
        return false;
    }


    private void streamVehicleToEs(Writer writer, String key, Map<String, Object> map) throws IOException {

        writer
                .append("{ \"index\" : { \"_id\" : \"")
                .append(key)
                .append("\" } }\n");

        gson.toJson(map, writer);
        writer.write("\n");
    }

    private void streamVehicleDeleteToEs(Writer writer, String key) throws IOException {

        writer
                .append("{ \"delete\" : { \"_id\" : \"")
                .append(key)
                .append("\" } }\n");
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
}

package hub.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hub.config.Config;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;


@SuppressFBWarnings
public class DataStoreFactory {

    public static Datastore createDealerCsDbDataStore() throws UnknownHostException {
        // parse list of hosts
        List<ServerAddress> mongoHosts = new ArrayList<ServerAddress>();
        String mongodbHosts = Config.get("mongodbHosts", "127.0.0.1");
        for (String url : mongodbHosts.split(":")) {
            ServerAddress serverAddress = new ServerAddress(url);
            mongoHosts.add(serverAddress);
        }

        MongoClient mongo = new MongoClient(mongoHosts);
        mongo.setReadPreference(ReadPreference.secondaryPreferred());
       // datastore.ensureIndexes(DetectorFraudProbability.class);
        // instantiate datastore
        Datastore datastore = new DatastoreImpl(new Morphia(), mongo, Config.get("mongodbName", "hub"));

        return datastore;
    }
}

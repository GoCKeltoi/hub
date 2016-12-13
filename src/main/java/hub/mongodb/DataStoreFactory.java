package hub.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;



public class DataStoreFactory {

    private String mongodbHosts;

    private String dealerCsDbName;

    public Datastore createDealerCsDbDataStore() throws UnknownHostException {
        // parse list of hosts
        List<ServerAddress> mongoHosts = new ArrayList<ServerAddress>();
        for (String url : mongodbHosts.split(":")) {
            ServerAddress serverAddress = new ServerAddress(url);
            mongoHosts.add(serverAddress);
        }

        MongoClient mongo = new MongoClient(mongoHosts);
        mongo.setReadPreference(ReadPreference.secondaryPreferred());

        // instantiate datastore
        Datastore datastore = new DatastoreImpl(new Morphia(), mongo, dealerCsDbName);

        return datastore;
    }
}

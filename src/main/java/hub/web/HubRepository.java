package hub.web;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import java.util.Map;

public class HubRepository {

    private final Datastore datastore;

    public HubRepository(Datastore datastore) {
        this.datastore = datastore;
    }


    public void delete(ObjectId id) {
        final Query<Event> query = datastore.createQuery(Event.class);
        query.field(Mapper.ID_KEY).equal(id);
        datastore.delete(query);
    }

    public long countAll() {
        return datastore.createQuery(Event.class).countAll();
    }
}

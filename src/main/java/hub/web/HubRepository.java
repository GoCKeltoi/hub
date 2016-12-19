package hub.web;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

public class HubRepository {

    private final Datastore datastore;

    public HubRepository(Datastore datastore) {
        this.datastore = datastore;
    }


    public void save(Event event){
        datastore.save(event);
    }

    public void delete(ObjectId id) {
        final Query<Event> query = datastore.createQuery(Event.class);
        query.field(Mapper.ID_KEY).equal(id);
        datastore.delete(query);
    }

    public long countAll() {
        return datastore.createQuery(Event.class).countAll();
    }

    public Event find(ObjectId objectId) {
        return datastore.createQuery(Event.class).field("_id").equal(objectId).get();
    }

  //  public List<CsProcessingHistoryEntry> findLastEntries(String userName, int limit) {
    //    return fsboCsDbDataStore.createQuery(CsProcessingHistoryEntry.class)
      //          .field(CsProcessingHistoryEntry.PROP_EMPLOYEE).equal(userName)
       //         .order("-" + CsProcessingHistoryEntry.PROP_PROCESSING_END_TIME).limit(limit).asList();
    //}
}

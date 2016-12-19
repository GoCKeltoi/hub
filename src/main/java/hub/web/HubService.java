package hub.web;

import org.bson.types.ObjectId;

public class HubService {

    private final HubRepository repository;

    public HubService(HubRepository repository){
        this.repository = repository;
    }

    public void save(Event e){
        repository.save(e);
    }

    public void delete(String id){
        repository.delete(new ObjectId(id));
    }


    public void find(String id) {
        repository.find(new ObjectId(id));
    }
}

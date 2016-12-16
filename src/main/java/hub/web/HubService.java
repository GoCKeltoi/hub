package hub.web;

public class HubService {

    private final HubRepository repository;

    public HubService(HubRepository repository){
        this.repository = repository;
    }
}

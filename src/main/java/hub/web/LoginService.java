package hub.web;

import org.bson.types.ObjectId;

public class LoginService {

    private final HubRepository repository;

    public LoginService(HubRepository repository){
        this.repository = repository;
    }

    public void save(Customer c){
        repository.saveCustomer(c);
    }

    //public void delete(String id){repository.delete(new ObjectId(id)); }

    public Customer find(String login, String password) {
        return repository.findCustomer(login, password);
    }
}

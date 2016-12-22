package hub.web;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

@SuppressFBWarnings
@Entity(value = "customer", noClassnameStored = true)
public class Customer {

    @Id
    private ObjectId id;

    private Date creationTime;

    private String login;

    private String password;

    private boolean active;

    private Customer() {
        // used by morphia
    }

    public Customer(String login, String password) {
        this.login = login;
        this.password = password;
        this.active = true;
    }

    public boolean matches(String login, String password){
        return this.login.equals(login) && this.password.equals(password);
    }

    public String getId(){
        return id.toString();
    }
}

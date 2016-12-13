package hub.server;

import static com.google.common.base.Throwables.propagate;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import de.mobile.inventorylistindexer.App;

class TomcatApp implements App {

    private final Tomcat t;

    public TomcatApp(Tomcat t) {
        this.t = t;
    }

    public void start() {
        try {
            t.start();
//            t.getServer().await();
        } catch (LifecycleException e) {
            propagate(e);
        }
    }

}

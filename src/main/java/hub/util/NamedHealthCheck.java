package hub.util;

import com.codahale.metrics.health.HealthCheck;

public abstract class NamedHealthCheck extends HealthCheck {

    public final String name;

    protected NamedHealthCheck(String name) {
        this.name = name;
    }

    public static NamedHealthCheck of(String name, HealthCheck check) {
        return new NamedHealthCheck(name) {
            @Override
            protected Result check() throws Exception {
                return check.execute();
            }
        };
    }

}

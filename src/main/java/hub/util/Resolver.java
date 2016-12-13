package hub.util;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resolver {

    private static final Logger logger = LoggerFactory.getLogger(Resolver.class);

    public static <T> T withRetry(Supplier<Optional<T>> supplier, Duration sleepInterval) {
        Optional<T> val = supplier.get();
        while (!val.isPresent()) {
            try {
                logger.info("Waiting for value to resolve...");
                Thread.sleep(sleepInterval.toMillis());
            } catch (InterruptedException e) {
            }
            val = supplier.get();
        }
        assert val.isPresent();
        return val.get();
    }

}

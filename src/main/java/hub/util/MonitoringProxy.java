package de.mobile.inventorylistindexer.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.reflect.AbstractInvocationHandler;

import rx.Observable;

@SuppressWarnings("PMD.TooManyMethods")
public class MonitoringProxy<T> extends AbstractInvocationHandler {
    private final T src;
    private final Observable.Transformer<Object, Object> transformer;
    private final SuccessMonitor successMonitor;
    private final FailureMonitor failureMonitor;
    private final String clazzName;

    MonitoringProxy(T src,
                    Class<T> clazz,
                    Observable.Transformer<Object, Object> transformer,
                    SuccessMonitor successMonitor,
                    FailureMonitor failureMonitor) {
        this.src = src;
        this.clazzName = clazz.getSimpleName();
        this.transformer = transformer;
        this.successMonitor = successMonitor;
        this.failureMonitor = failureMonitor;
    }

    @Override
    public Object handleInvocation(Object proxy, Method m, Object[] args) throws Throwable {
        final Object result;

        String useCase = MetricRegistry.name(clazzName, m.getName());
        long started = System.currentTimeMillis();

        try {
            result = m.invoke(src, args);

            if (proxy instanceof Servlet
                    && m.getName().equals("service")
                    && args.length == 2
                    && args[1] instanceof HttpServletResponse) {
                final int status = ((HttpServletResponse) args[1]).getStatus();
                useCase = MetricRegistry.name(useCase, String.valueOf(status));
            }

            if (result instanceof Observable) {
                return monitorObservable((Observable) result, useCase, started);
            } else {
                long elapsed = System.currentTimeMillis() - started;
                successMonitor.accept(useCase, elapsed);
            }
        } catch (InvocationTargetException e) {
            failureMonitor.accept(useCase, e.getTargetException());
            throw e.getTargetException();
        }

        return result;
    }

    Observable<?> monitorObservable(Observable<?> src, String useCase, long startedTimestamp) {
        return src
                .compose(transformer)
                .doOnCompleted(() -> {
                    long elapsed = System.currentTimeMillis() - startedTimestamp;
                    successMonitor.accept(useCase, elapsed);
                })
                .doOnError(t -> {
                    failureMonitor.accept(useCase, t);
                });
    }

    /**
     * A success monitor is a consumer for a use case (String) and a duration in milliseconds (Long).
     */
    public interface SuccessMonitor extends BiConsumer<String, Long> {
    }

    /**
     * A failure monitor is a consumer for a use case (String) and a throwable.
     */
    public interface FailureMonitor extends BiConsumer<String, Throwable> {
    }

    @SuppressWarnings({"unused", "PMD.TooManyMethods"})
    public static final class Builder<T> {
        private T src;
        private Class<T> clazz;
        private Observable.Transformer<Object, Object> transformer;
        private MetricRegistry metrics;
        private SuccessMonitor successMonitor;
        private FailureMonitor failureMonitor;
        private String ns;
        private boolean logRetrofitErrors = false;

        public Builder<T> src(T value) {
            this.src = value;
            return this;
        }

        public Builder<T> clazz(Class<T> value) {
            this.clazz = value;
            return this;
        }

        public Builder<T> transformer(Observable.Transformer<Object, Object> value) {
            this.transformer = value;
            return this;
        }

        public Builder<T> namespace(String value) {
            this.ns = value;
            return this;
        }

        public Builder<T> metricRegistry(MetricRegistry value) {
            this.metrics = value;
            return this;
        }

        public Builder<T> successMonitor(SuccessMonitor value) {
            this.successMonitor = value;
            return this;
        }

        public Builder<T> failureMonitor(FailureMonitor value) {
            this.failureMonitor = value;
            return this;
        }

        public Builder<T> logRetrofitErrors(boolean value) {
            this.logRetrofitErrors = value;
            return this;
        }

        private SuccessMonitor buildSuccessMonitor() {
            checkNotNull(ns);
            checkNotNull(metrics);

            return (useCase, elapsedMillis) -> {
                String metricsName = MetricRegistry.name(ns, useCase);
                Timer t = metrics.timer(metricsName);
                t.update(elapsedMillis, MILLISECONDS);
            };
        }

        private FailureMonitor buildFailureMonitor() {
            checkNotNull(ns);
            checkNotNull(metrics);
            checkNotNull(clazz);
            Logger log = LoggerFactory.getLogger(clazz);

            return (useCase, throwable) -> {
                log.info("Unexpected error for use case={}, throwable={}, msg={}",
                        useCase,
                        throwable.getClass().getName(),
                        throwable.getMessage());
                String metricsName = MetricRegistry.name(ns, useCase, "error");
                Counter c = metrics.counter(metricsName);
                c.inc();
            };
        }

        private FailureMonitor buildRetrofitAwareFailureMonitor() {
            checkNotNull(ns);
            checkNotNull(metrics);
            checkNotNull(clazz);
            Logger log = LoggerFactory.getLogger(clazz);

            return (useCase, throwable) -> {
                log.info("Unexpected error for use case={}, throwable={}, msg={}",
                        useCase,
                        throwable.getClass().getName(),
                        throwable.getMessage());

                // TODO: migrate this class to Retrofit 2
//                final String metricName = Optional.of(throwable)
//                        .filter(ex -> ex instanceof RetrofitError)
//                        .map(ex -> {
//                            final RetrofitError retrofitError = (RetrofitError) ex;
//                            return MetricRegistry.name(ns, useCase, "retrofit_error", retrofitError.getKind().name());
//                        })
//                        .orElse(MetricRegistry.name(ns, useCase, "error"));
//
//                Counter c = metrics.counter(metricName);

                Counter c = metrics.counter(MetricRegistry.name(ns, useCase, "error"));
                c.inc();
            };
        }

        public T build() {
            checkNotNull(src);
            checkNotNull(clazz);

            SuccessMonitor sm = Optional
                    .ofNullable(successMonitor)
                    .orElse(buildSuccessMonitor());

            FailureMonitor fm = Optional
                    .ofNullable(failureMonitor)
                    .orElse(logRetrofitErrors ? buildRetrofitAwareFailureMonitor() : buildFailureMonitor());

            Observable.Transformer<Object, Object> t = Optional
                    .ofNullable(transformer)
                    .orElse(src -> src);

            return of(src, new MonitoringProxy<>(src, clazz, t, sm, fm));
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    @SuppressWarnings("unchecked")
    public static <T> T of(T src, MonitoringProxy<T> m) {
        return (T) Proxy.newProxyInstance(
                src.getClass().getClassLoader(),
                src.getClass().getInterfaces(),
                m);
    }
}


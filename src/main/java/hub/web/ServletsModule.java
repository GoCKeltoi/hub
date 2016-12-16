package hub.web;

import javax.inject.Singleton;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;

import hub.Route;
import hub.indexer.FullIndexBuilder;

@Module
public class ServletsModule {

    @Provides(type = Provides.Type.SET)
    Route providePing() {
        return new Route("/internal/ping", new PingServlet());
    }

    @Provides(type = Provides.Type.SET)
    Route provideReleaseInfo() {
        return new Route("/internal/release-info", new ReleaseInfoServlet() {});
    }

    @Provides(type = Provides.Type.SET)
    Route provideRecreateIndex(FullIndexBuilder reindexer, MetricRegistry mr) {
        return new Route("/internal/reindex", new ReIndexServlet(reindexer, mr));
    }

    @Provides(type = Provides.Type.SET)
    Route hubRoute(HubServlet hubServlet) {
        return new Route("/hub", hubServlet);
    }

    @Provides
    @Singleton
    HubServlet hubServlet(Gson gson) {
        return new HubServlet(gson);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
//                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
    }
}

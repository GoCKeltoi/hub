package hub.util.http;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Interceptor;
import okhttp3.Response;

public class RequestResponseLogger implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLogger.class);

    private final String apiName;

    public RequestResponseLogger(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        logRequest(chain);
        Response rsp = chain.proceed(chain.request());
        logResponse(rsp);
        return rsp;
    }

    private void logResponse(Response response) {
        if (response.code() >= 400) {
            logger.error("{} - OkHttp response with status code: {}", apiName, response.code());
        } else {
            logger.info("{} - OkHttp response with status code: {}", apiName, response.code());
        }

        if (response.headers().size() > 0) {
            logger.info("{} - Response headers: \n {}", apiName, response.headers());
        }
    }

    private void logRequest(Chain chain) {
        if (chain.request().headers().size() > 0) {
            logger.info("{} - Request headers: \n {}", apiName, chain.request().headers());
        }
        logger.info("{} - Full query: {}", apiName, chain.request().toString());
    }
}

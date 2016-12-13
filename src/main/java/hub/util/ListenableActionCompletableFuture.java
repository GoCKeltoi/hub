package hub.util;

import java.util.concurrent.CompletableFuture;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;

public class ListenableActionCompletableFuture {

    public static <T> CompletableFuture<T> from(final ListenableActionFuture<T> future) {
        final CompletableFuture<T> completableFuture = new CompletableFuture<>();
        future.addListener(
            new ActionListener<T>() {
                @Override
                public void onResponse(T t) {
                    completableFuture.complete(t);
                }

                @Override
                public void onFailure(Throwable e) {
                    completableFuture.completeExceptionally(e);
                }
            }
        );
        return completableFuture;
    }
}

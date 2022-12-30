package net.orbyfied.coldlib.util.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A callable function that can be
 * externally handled. This can be
 * invoked both with or without a value.
 *
 * @param <V> The value type.
 */
public interface Callback<V> extends Callable<V> {

    /**
     * Allows one handler and the same
     * future to be in use at a time.
     *
     * @param <V> The value type.
     * @return The callback.
     */
    static <V> Callback<V> mono() {
        return new Callback<>() {
            Consumer<V> consumer;
            CompletableFuture<V> future;

            @Override
            public Callback<V> then(Consumer<V> consumer) {
                this.consumer = consumer;
                return this;
            }

            @Override
            public CompletableFuture<V> await() {
                return future != null ? future : (future = new CompletableFuture<>());
            }

            @Override
            public void call(V value) {
                if (consumer != null)
                    consumer.accept(value);
                if (future != null)
                    future.complete(value);
            }
        };
    }

    /**
     * Allows multiple handlers and futures
     * to be in use at the same time.
     *
     * @param <V> The value type.
     * @return The callback.
     */
    static <V> Callback<V> multi() {
        return new Callback<V>() {
            // the handlers
            List<Consumer<V>> consumers = new ArrayList<>();
            // the futures
            List<CompletableFuture<V>> futures = new ArrayList<>();

            @Override
            public Callback<V> then(Consumer<V> consumer) {
                consumers.add(consumer);
                return this;
            }

            @Override
            public CompletableFuture<V> await() {
                CompletableFuture<V> future = new CompletableFuture<>();
                futures.add(future);
                return future;
            }

            @Override
            public void call(V value) {
                {
                    final int l = consumers.size();
                    for (int i = 0; i < l; i++)
                        consumers.get(i).accept(value);
                }

                {
                    final int l = futures.size();
                    for (int i = 0; i < l; i++)
                        futures.get(i).complete(value);
                    futures.clear();
                }
            }
        };
    }

    /////////////////////////////////////////

    /**
     * Register a handler for the value
     * when called. This may replace an
     * existing handler or append a new
     * one to the end of the pipeline
     * depending on the implementation.
     *
     * @param consumer The handler.
     * @return This.
     */
    Callback<V> then(Consumer<V> consumer);

    /**
     * Await a call by accepting an
     * {@link CompletableFuture}.
     *
     * @return The future.
     */
    CompletableFuture<V> await();

}

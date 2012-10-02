package com.atlassian.fugue;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;

/**
 * Library of utility {@link Promise} functions
 *
 * @since 1.2
 */
@Beta
public final class Promises {
    private Promises() {}

    /**
     * Returns a new promise representing the status of a list of other promises.
     *
     * @param promises The promises that the new promise should track
     * @return The new, aggregate promise
     */
    public static <V> Promise<List<V>> when(Promise<? extends V>... promises) {
        return when(asList(promises));
    }

    /**
     * Returns a new promise representing the status of a list of other promises.
     *
     * @param promises The promises that the new promise should track
     * @return The new, aggregate promise
     */
    public static <V> Promise<List<V>> when(Iterable<? extends Promise<? extends V>> promises) {
        return forListenableFuture(Futures.<V>allAsList(promises));
    }

    /**
     * Creates a new, resolved promise for the specified concrete value.
     *
     * @param instance The value for which a promise should be created
     * @return The new promise
     */
    public static <V> Promise<V> ofInstance(V instance) {
        return new PromiseOfInstance<V>(instance);
    }

    /**
     * Creates a new, rejected promise from the given Throwable and result type.
     *
     * @param instance The throwable
     * @param resultType The result type
     * @return The new promise
     */
    public static <V> Promise<V> rejected(Throwable instance, Class<V> resultType) {
        return new PromiseOfInstance<V>(instance);
    }

    /**
     * Creates a promise from the given future.
     *
     * @param future The future delegate for the new promise
     * @return The new promise
     */
    public static <V> Promise<V> forListenableFuture(ListenableFuture<V> future) {
        return new PromiseOfListenableFuture<V>(future);
    }

    /**
     * Transforms a promise from one type to another by way of a transformation function.
     *
     * @param promise The promise to transform
     * @param function THe transformation function
     * @return The promise resulting from the transformation
     */
    public static <I, O> Promise<O> transform(Promise<I> promise, Function<? super I, ? extends O> function) {
        return forListenableFuture(Futures.transform(promise, function));
    }

    /**
     * Creates a new <code>PromiseCallback</code> that forwards a promise's fail events to
     * the specified future delegate's <code>setException</code> method -- that is, the new
     * callback rejects the delegate future if invoked.
     *
     * @param delegate The future to be rejected on a fail event
     * @return The fail callback
     */
    public static PromiseCallback<Throwable> reject(final SettableFuture<?> delegate) {
        return new PromiseCallback<Throwable>()
        {
            @Override
            public void handle(Throwable t)
            {
                delegate.setException(t);
            }
        };
    }

    private final static class PromiseOfInstance<V> implements Promise<V>
    {
        private final Promise<V> delegate;

        public PromiseOfInstance(Throwable throwable)
        {
            SettableFuture<V> future = SettableFuture.create();
            future.setException(throwable);
            delegate = forListenableFuture(future);
        }

        public PromiseOfInstance(V value)
        {
            SettableFuture<V> future = SettableFuture.create();
            future.set(value);
            delegate = forListenableFuture(future);
        }

        @Override
        public V claim()
        {
            return delegate.claim();
        }

        public Promise<V> done(PromiseCallback<V> callback)
        {
            return delegate.done(callback);
        }

        @Override
        public Promise<V> fail(PromiseCallback<Throwable> callback)
        {
            return delegate.fail(callback);
        }

        public Promise<V> then(FutureCallback<V> callback)
        {
            return delegate.then(callback);
        }

        @Override
        public void addListener(Runnable listener, Executor executor)
        {
            delegate.addListener(listener, executor);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning)
        {
            return delegate.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled()
        {
            return delegate.isCancelled();
        }

        @Override
        public boolean isDone()
        {
            return delegate.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException
        {
            return delegate.get();
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
        {
            return delegate.get(timeout, unit);
        }
    }

    private static final class PromiseOfListenableFuture<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements Promise<V>
    {
        public PromiseOfListenableFuture(ListenableFuture<V> delegate)
        {
            super(delegate);
        }

        @Override
        public V claim()
        {
            try
            {
                return get();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            catch (ExecutionException e)
            {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException)
                {
                    throw (RuntimeException) cause;
                }
                else
                {
                    throw new RuntimeException(cause);
                }
            }
        }

        @Override
        public Promise<V> done(final PromiseCallback<V> callback)
        {
            Futures.addCallback(this, new FutureCallback<V>()
            {
                @Override
                public void onSuccess(V result)
                {
                    callback.handle(result);
                }

                @Override
                public void onFailure(Throwable t)
                {
                    // no-op
                }
            });
            return this;
        }

        @Override
        public Promise<V> fail(final PromiseCallback<Throwable> callback)
        {
            Futures.addCallback(this, new FutureCallback<V>()
            {
                @Override
                public void onSuccess(V result)
                {
                    // no-op
                }

                @Override
                public void onFailure(Throwable t)
                {
                    callback.handle(t);
                }
            });
            return this;
        }

        @Override
        public Promise<V> then(FutureCallback<V> callback)
        {
            Futures.addCallback(this, callback);
            return this;
        }
    }
}

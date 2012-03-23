package com.atlassian.fugue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Function;

public class Futures {
  public static <A, B> Future<B> transform(final Future<A> future, final Function<A, B> f) {
    return new Mapped<A, B>(future, f);
  }

  private static class Mapped<A, B> implements Future<B> {
    private final Future<A> future;
    private final Function<A, B> f;

    public Mapped(final Future<A> future, final Function<A, B> f) {
      this.future = future;
      this.f = f;
    }

    @Override
    public B get() throws InterruptedException, ExecutionException {
      return f.apply(future.get());
    }

    @Override
    public B get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return f.apply(future.get(timeout, unit));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return future.isCancelled();
    }

    @Override
    public boolean isDone() {
      return future.isDone();
    }
  }
}

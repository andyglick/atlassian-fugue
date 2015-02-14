package com.atlassian.fugue.mango.Function;

/**
 * Created by anund on 2/14/15.
 */
public interface Supplier<A> extends Function<Object, A> {
  abstract public A get();

  public static abstract class AbstractSupplier<A> implements Supplier<A> {
    public A apply(Object v) {
      return get();
    }

    abstract public A get();
  }
}
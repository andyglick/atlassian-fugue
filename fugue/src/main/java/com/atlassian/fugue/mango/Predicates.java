package com.atlassian.fugue.mango;

import com.atlassian.fugue.mango.Function.Predicate;

/**
 * Created by anund on 2/14/15.
 */
public class Predicates {
  private Predicates() {}

  public static <A> Predicate<A> not(final Predicate<A> predicate) {
    return new Predicate<A>() {
      @Override public Boolean apply(A a) {
        return !predicate.apply(a);
      }
    };
  }

  public static <A> Predicate<A> equalTo(final A seed) {
    Preconditions.checkNotNull(seed);
    return new Predicate<A>() {
      @Override public Boolean apply(A a) {
        return seed.equals(a);
      }
    };
  }

  @SuppressWarnings("unchecked") public static <A> Predicate<A> alwaysTrue() {
    return (Predicate<A>) AlwaysTrue.INSTANCE;
  }

  @SuppressWarnings("unchecked") public static <A> Predicate<A> alwaysFalse() {
    return (Predicate<A>) AlwaysFalse.INSTANCE;
  }

  enum AlwaysTrue implements Predicate<Object> {
    INSTANCE;

    public Boolean apply(Object o) {
      return Boolean.TRUE;
    }
  }

  enum AlwaysFalse implements Predicate<Object> {
    INSTANCE;

    public Boolean apply(Object o) {
      return Boolean.FALSE;
    }
  }
}

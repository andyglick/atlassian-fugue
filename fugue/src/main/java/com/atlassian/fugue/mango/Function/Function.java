package com.atlassian.fugue.mango.Function;

/**
 * Created by anund on 2/14/15.
 */
public interface Function<A, B> {
  public B apply(A a);
}

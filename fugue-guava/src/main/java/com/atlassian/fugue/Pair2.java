package com.atlassian.fugue;

/**
 * Created by anund on 3/30/15.
 */
public class Pair2 {
  /**
   * Zips two iterables into a single iterable that produces {@link Pair pairs}.
   *
   * @param <A> LHS type
   * @param <B> RHS type
   * @param as left values
   * @param bs right values
   * @return an {@link Iterable iterable} of pairs, only as long as the shortest
   * input iterable.
   * @since 1.1
   */
  public static <A, B> Iterable<Pair<A, B>> zip(final Iterable<A> as, final Iterable<B> bs) {
    return Iterables.zip(as, bs);
  }
}

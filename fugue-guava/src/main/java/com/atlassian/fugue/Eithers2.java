package com.atlassian.fugue;

import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Iterables.transform;

public class Eithers2 {
  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param eithers an Iterable of either values
   * @return either the iterable of right values, or the first left encountered.
   */
  public static <L, R> Either<L, Iterable<R>> sequenceRight(final Iterable<Either<L, R>> eithers) {
    ImmutableList.Builder<R> it = ImmutableList.builder();
    for (final Either<L, R> e : eithers) {
      if (e.isLeft()) {
        return e.left().<Iterable<R>> as();
      }
      it.add(e.right().get());
    }
    return Either.right((Iterable<R>) it.build());
  }

  /**
   * Collect the left values if there are only lefts, otherwise return the
   * first right encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param eithers an Iterable of either values
   * @return either the iterable of left values, or the first right encountered.
   */
  public static <L, R> Either<Iterable<L>, R> sequenceLeft(final Iterable<Either<L, R>> eithers) {
    Iterable<L> it = ImmutableList.of();
    for (final Either<L, R> e : eithers) {
      if (e.isRight()) {
        return e.right().<Iterable<L>> as();
      }
      it = com.google.common.collect.Iterables.concat(it, e.left());
    }
    return Either.left(it);
  }

  /**
   * Takes an {@link Iterable} of {@link Either eithers}, and collects the left
   * values of every either which has a left value
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param it iterable of eithers to filter and transform from
   * @return the left values contained in the contents of it
   */
  public static <L, R> Iterable<L> filterLeft(Iterable<Either<L, R>> it) {
    return Iterables.collect(it, Eithers.<L, R> leftMapper());
  }

  /**
   * Takes an {@link Iterable} of {@link Either eithers}, and collects the right
   * values of every either which has a left value
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param it iterable of eithers to filter and transform from
   * @return the right values contained in the contents of it
   */
  public static <L, R> Iterable<R> filterRight(Iterable<Either<L, R>> it) {
    return Options2.flatten(transform(it, Eithers.<L, R> rightMapper()::apply));
  }
}

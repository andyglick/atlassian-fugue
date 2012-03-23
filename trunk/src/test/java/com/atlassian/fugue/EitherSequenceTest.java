package com.atlassian.fugue;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Either.sequenceLeft;
import static com.atlassian.fugue.Either.sequenceRight;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;

@SuppressWarnings("unchecked") public class EitherSequenceTest {
  @Test public void sequenceRights() {
    final ImmutableList<Either<Object, Integer>> eithers = ImmutableList.of(right(1), right(2), right(3));
    assertThat(sequenceRight(eithers).right().get(), contains(1, 2, 3));
  }

  @Test public void sequenceRightWithLeft() {
    final Iterable<Either<String, Integer>> eithers = build(Build.<String, Integer> r(1), Build.<String, Integer> l("2"), Build
      .<String, Integer> r(3));
    assertThat(sequenceRight(eithers), is(Build.<String, Iterable<Integer>> l("2")));
  }

  @Test public void sequenceLefts() {
    final ImmutableList<Either<Integer, Object>> eithers = ImmutableList.of(left(1), left(2), left(3));
    assertThat(sequenceLeft(eithers).left().get(), contains(1, 2, 3));
  }

  @Test public void sequenceLeftWithRight() {
    final Iterable<Either<String, Integer>> eithers = build(Build.<String, Integer> l("1"), Build.<String, Integer> r(2), Build
      .<String, Integer> l("3"));
    assertThat(sequenceLeft(eithers), is(Build.<Iterable<String>, Integer> r(2)));
  }

  static class Build {
    static <L, R> Either<L, R> r(final R i) {
      return right(i);
    }

    static <L, R> Either<L, R> l(final L s) {
      return left(s);
    }
  }

  static <L, R> Iterable<Either<L, R>> build(final Either<L, R>... e) {
    return Arrays.asList(e);
  }
}

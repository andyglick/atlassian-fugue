/*
   Copyright 2011 Atlassian

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.atlassian.fugue.collect;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.collect.EithersCollect.sequenceLeft;
import static com.atlassian.fugue.collect.EithersCollect.sequenceRight;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import com.atlassian.fugue.Either;
import org.junit.Test;

@SuppressWarnings("unchecked") public class EitherSequenceTest {
  @Test public void sequenceRights() {
    final List<Either<Object, Integer>> eithers = Arrays.asList(right(1), right(2), right(3));
    assertThat(sequenceRight(eithers).right().get(), contains(1, 2, 3));
  }

  @Test public void sequenceRightWithLeft() {
    final Iterable<Either<String, Integer>> eithers = build(Build.<String, Integer> r(1),
      Build.<String, Integer> l("2"), Build.<String, Integer> r(3));
    assertThat(sequenceRight(eithers), is(Build.<String, Iterable<Integer>> l("2")));
  }

  @Test public void sequenceLefts() {
    final List<Either<Integer, Object>> eithers = Arrays.asList(left(1), left(2), left(3));
    assertThat(sequenceLeft(eithers).left().get(), contains(1, 2, 3));
  }

  @Test public void sequenceLeftWithRight() {
    final Iterable<Either<String, Integer>> eithers = build(Build.<String, Integer> l("1"),
      Build.<String, Integer> r(2), Build.<String, Integer> l("3"));
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

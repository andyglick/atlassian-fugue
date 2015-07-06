/*
   Copyright 2015 Atlassian

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

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Eithers;
import com.atlassian.fugue.Options;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class contains useful methods for dealing with Iterables that contain Either
 *
 * @since 3.0
 */
public class EithersCollect {

  /**
   * Takes an {@link Iterable} of {@link Either eithers}, and collects the left
   * values of every either which has a left value
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param it iterable of eithers to filter and transform from
   * @return the left values contained in the contents of it
   * @since 3.0
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
   * @since 3.0
   */
  public static <L, R> Iterable<R> filterRight(Iterable<Either<L, R>> it) {
    return Options.flatten(Iterables.transform(it, Eithers.<L, R>rightMapper()));
  }

  /**
   * Collect the right values if there are only rights, otherwise return the
   * first left encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param eithers an Iterable of either values
   * @return either the iterable of right values, or the first left encountered.
   * @since 3.0
   */
  public static <L, R> Either<L, Iterable<R>> sequenceRight(final Iterable<Either<L, R>> eithers) {
    ArrayList<R> rs = new ArrayList<>();
    for (final Either<L, R> e : eithers) {
      if (e.isLeft()) {
        return Either.left(e.left().get());
      }
      rs.add(e.right().get());
    }
    return Either.right(Collections.unmodifiableList(rs));
  }

  /**
   * Collect the left values if there are only lefts, otherwise return the first
   * right encountered.
   *
   * @param <L> the LHS type
   * @param <R> the RHS type
   * @param eithers an Iterable of either values
   * @return either the iterable of left values, or the first right encountered.
   * @since 3.0
   */
  public static <L, R> Either<Iterable<L>, R> sequenceLeft(final Iterable<Either<L, R>> eithers) {
    ArrayList<L> ls = new ArrayList<>();
    for (final Either<L, R> e : eithers) {
      if (e.isRight()) {
        return Either.right(e.right().get());
      }
      ls.add(e.left().get());
    }
    return Either.left(Collections.unmodifiableList(ls));
  }
}

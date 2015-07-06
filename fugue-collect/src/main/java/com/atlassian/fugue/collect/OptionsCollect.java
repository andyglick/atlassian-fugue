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

import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

import static com.atlassian.fugue.collect.Iterables.filter;
import static com.atlassian.fugue.collect.Iterables.transform;
import static com.atlassian.fugue.Option.none;

/**
 * Utility methods for working with iterables of options.
 *
 * @since 3.0
 */
public class OptionsCollect {

  /**
   * Find the first option that {@link Option#isDefined() isDefined}, or if
   * there aren't any, then none.
   *
   * @param <A> the contained type
   * @param options an Iterable of options to search through
   * @return the first defined option, or none if there aren't any
   * @since 1.1
   */
  public static <A> Option<A> find(final Iterable<Option<A>> options) {
    for (final Option<A> option : options) {
      if (option.isDefined()) {
        return option;
      }
    }
    return none();
  }

  /**
   * Filter out undefined options.
   *
   * @param <A> the contained type
   * @param options many options that may or may not be defined
   * @return the filtered options
   * @since 1.1
   */
  public static <A> Iterable<Option<A>> filterNone(final Iterable<Option<A>> options) {
    return filter(options, Maybe::isDefined);
  }

  /**
   * Flattens an {@link Iterable} of {@link Option options} into an iterable of
   * the things, filtering out any nones.
   *
   * @param <A> the contained type
   * @param options the iterable of options
   * @return an {@link Iterable} of the contained type
   * @since 1.1
   */
  public static <A> Iterable<A> flatten(final Iterable<Option<A>> options) {
    return transform(filterNone(options), Maybe::get);
  }
}

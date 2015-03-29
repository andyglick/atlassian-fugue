package com.atlassian.fugue;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

//TODO name this something that avoids needing to import as a fully qualified path
public class Options2 {

  /**
   * Filter out undefined options.
   *
   * @param <A> the contained type
   * @param options many options that may or may not be defined
   * @return the filtered options
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
   */
  // TODO add a version of flatten that takes Option<Option<A>>
  public static <A> Iterable<A> flatten(final Iterable<Option<A>> options) {
    return transform(filterNone(options), Maybe::get);
  }

}

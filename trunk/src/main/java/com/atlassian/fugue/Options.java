package com.atlassian.fugue;

import static com.atlassian.fugue.Option.defined;
import static com.atlassian.fugue.Option.none;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;

/**
 * Utility methods for working with iterables of options.
 * 
 * @since 1.1
 */
public class Options {
  private Options() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  /**
   * Find the first option that isDefined, or if there aren't any, then none.
   * 
   * @param <A> the contained type
   * @param options an Iterable of options to search through
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
   */
  public static <A> Iterable<Option<A>> filterNone(final Iterable<Option<A>> options) {
    return filter(options, defined());
  }

  /**
   * Flattens an {@link Iterable} of {@link Option options} into an iterable of
   * the things, filtering out any nones.
   * 
   * @param <A> the contained type
   * @param options the iterable of options
   * @return an {@link Iterable} of the contained type
   */
  public static <A> Iterable<A> flatten(final Iterable<Option<A>> options) {
    return transform(filterNone(options), new SomeAccessor<A>());
  }

  /**
   * Function for accessing the contents of defined options, errors if the
   * option is not defined.
   */
  static class SomeAccessor<A> implements Function<Option<A>, A> {
    @Override public A apply(final Option<A> from) {
      return from.get();
    }
  }
}

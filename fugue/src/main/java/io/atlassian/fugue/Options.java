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
package io.atlassian.fugue;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.atlassian.fugue.Iterables.filter;
import static io.atlassian.fugue.Iterables.map;
import static io.atlassian.fugue.Option.none;
import static java.util.Objects.requireNonNull;

/**
 * Utility methods for working with {@link Option options}.
 *
 * @since 1.1
 */
public class Options {
  private Options() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  /**
   * Find the first option that {@link io.atlassian.fugue.Option#isDefined()
   * isDefined}, or if there aren't any, then none.
   *
   * @param <A> the contained type
   * @param options an Iterable of options to search through
   * @return the first defined option, or none if there aren't any
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
   * Upcasts an {@link Option option} of type A to an option of its super type
   * AA.
   *
   * @param o the source option
   * @param <AA> the super type of the contained type
   * @return an option of the super type
   * @since 2.0
   */
  public static <AA, A extends AA> Option<AA> upcast(final Option<A> o) {
    return o.map(Functions.<AA> identity());
  }

  /**
   * Lifts a function that takes an A and returns a B into a function that takes
   * an option of A and returns an option of B.
   *
   * @param f the original function to be lifted, must not be null
   * @param <A> the input type of the original function
   * @param <B> the result type of the original function
   * @return a function that takes an option of type A and returns an option of
   * type B
   * @since 2.0
   */
  public static <A, B> Function<Option<A>, Option<B>> lift(final Function<A, B> f) {
    requireNonNull(f);
    return oa -> oa.map(f);
  }

  /**
   * Returns a function that will lift a function that takes an A and returns a
   * B into a function that takes an option of A and returns an option of B.
   *
   * @param <A> the input type of the function that can be lifted
   * @param <B> the result type of the function that can be lifted
   * @return a function that can lift a function of input type A and result type
   * B into Option
   * @since 2.0
   */
  public static <A, B> Function<Function<A, B>, Function<Option<A>, Option<B>>> lift() {
    return Options::lift;
  }

  /**
   * Lifts a predicate that takes an A into a predicate that takes an option of
   * A.
   *
   * @param pred the original predicate to be lifted, must not be null
   * @param <A> the input type of the predicate
   * @return a predicate that takes an option of type A
   * @since 2.2
   */
  public static <A> Predicate<Option<A>> lift(final Predicate<? super A> pred) {
    requireNonNull(pred);
    return oa -> oa.exists(pred);
  }

  /**
   * Applies an option of A to an option of a function with input type A and
   * result type B and return an option of B.
   *
   * @param oa an option of the argument to the function
   * @param of an option of a function that takes an A and returns a B
   * @param <A> the input type of the function wrapped in the option 'of'
   * @param <B> the result type of the function wrapped in the option 'of'
   * @return an option of B
   * @since 2.0
   */
  public static <A, B> Option<B> ap(final Option<A> oa, final Option<Function<A, B>> of) {
    return of.fold(Option.<B> noneSupplier(), Functions.compose(Functions.<Option<A>, Option<B>> apply(oa), Options.<A, B> lift()));
  }

  /**
   * Lifts a function that takes an A and a B and returns a C into a function
   * that takes an option of A and an option of B and returns an option of C.
   *
   * @param f2 the original function to be lifted
   * @param <A> the input type of the first argument of the original function
   * @param <B> the input type of the second argument of the original function
   * @param <C> the result type of the original function
   * @return a function that takes an option of type A and an option of B and
   * returns an option of type C
   * @since 2.0
   */
  public static <A, B, C> BiFunction<Option<A>, Option<B>, Option<C>> lift2(final BiFunction<A, B, C> f2) {
    final Function<A, Function<B, C>> curried = Functions.curried(f2);
    final Function<Option<A>, Option<Function<B, C>>> lifted = lift(curried);
    return (oa, ob) -> {
      final Option<Function<B, C>> ofbc = lifted.apply(oa);
      return Options.ap(ob, ofbc);
    };
  }

  /**
   * Returns a function that will lift a function that takes an A and a B and
   * returns a C into a function that takes an option of A and an option of B
   * and returns an option of C.
   *
   * @param <A> the input type of the first argument of the function that can be
   * lifted
   * @param <B> the input type of the second argument of the function that can
   * be lifted
   * @param <C> the result type of the function that can be lifted
   * @return a function that can lift a function of input type A and B and
   * result type C into Option
   * @since 2.0
   */
  public static <A, B, C> Function<BiFunction<A, B, C>, BiFunction<Option<A>, Option<B>, Option<C>>> lift2() {
    return Options::lift2;
  }

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
   * Flattens an {@link java.lang.Iterable} of {@link Option options} into an
   * iterable of the things, filtering out any nones.
   *
   * @param <A> the contained type
   * @param options the iterable of options
   * @return an {@link java.lang.Iterable} of the contained type
   */
  public static <A> Iterable<A> flatten(final Iterable<Option<A>> options) {
    return map(filterNone(options), Maybe::get);
  }

  /**
   * Function for wrapping values in a Some or None.
   *
   * @param <A> the contained type
   * @return a {@link java.util.function.Function} to wrap values
   * @since 3.0
   */
  public static <A> Function<A, Option<A>> toOption() {
    return Option::option;
  }

  /**
   * Turn a null producing function into one that returns an option instead.
   *
   * @param nullProducing the function that may return null
   * @return a function that turns nulls into None, and wraps non-null values in
   * Some.
   * @since 3.0
   * @param <A> input type to the function.
   * @param <B> output type of the function.
   */
  public static <A, B> Function<A, Option<B>> nullSafe(final Function<A, B> nullProducing) {
    return nullProducing.andThen(toOption());
  }
}

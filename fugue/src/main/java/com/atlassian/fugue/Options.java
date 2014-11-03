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
package com.atlassian.fugue;

import static com.atlassian.fugue.Option.defined;
import static com.atlassian.fugue.Option.none;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

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
   * Filter out undefined options.
   * 
   * @param <A> the contained type
   * @param options many options that may or may not be defined
   * @return the filtered options
   */
  public static <A> Iterable<Option<A>> filterNone(final Iterable<Option<A>> options) {
    return filter(options, Option.<A> defined());
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

  /**
   * Upcasts an {@link Option option} of type A to an option of its super type
   * AA.
   * 
   * @param o the source option
   * @param <AA> the super type of the contained type
   * @param <A> the contained type
   * @return an option of the super type
   * @since 2.0
   */
  public static <AA, A extends AA> Option<AA> upcast(Option<A> o) {
    return o.map(Functions.<AA> identity());
  }

  /**
   * Lifts a function that takes an A and returns a B into a function that takes
   * an option of A and returns an option of B.
   * 
   * @param f the original function to be lifted
   * @param <A> the input type of the original function
   * @param <B> the result type of the original function
   * @return a function that takes an option of type A and returns an option of
   * type B
   * @since 2.0
   */
  public static <A, B> Function<Option<A>, Option<B>> lift(final Function<A, B> f) {
    checkNotNull(f);
    return new Function<Option<A>, Option<B>>() {
      @Override public Option<B> apply(Option<A> oa) {
        return oa.map(f);
      }
    };
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
    return new Function<Function<A, B>, Function<Option<A>, Option<B>>>() {
      @Override public Function<Option<A>, Option<B>> apply(Function<A, B> f) {
        return lift(f);
      }
    };
  }
  
  /**
   * Lifts a predicate that takes an A into a predicate that takes
   * an option of A.
   * 
   * @param pred the original predicate to be lifted
   * @param <A> the input type of the predicate
   * @return a predicate that takes an option of type A 
   * @since 2.2
   */
  public static <A> Predicate<Option<A>> lift(final Predicate<? super A> pred) {
    checkNotNull(pred);
    return new Predicate<Option<A>>() {
      @Override public boolean apply(Option<A> oa) {
        return oa.exists(pred);
      }
    };
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
  public static <A, B> Option<B> ap(final Option<A> oa, Option<Function<A, B>> of) {
    return of.fold(Option.<B> noneSupplier(),
      com.google.common.base.Functions.compose(Functions.<Option<A>, Option<B>> apply(oa), Options.<A, B> lift()));
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
  public static <A, B, C> Function2<Option<A>, Option<B>, Option<C>> lift2(Function2<A, B, C> f2) {
    Function<A, Function<B, C>> curried = Functions.curried(f2);
    final Function<Option<A>, Option<Function<B, C>>> lifted = lift(curried);
    return new Function2<Option<A>, Option<B>, Option<C>>() {
      @Override public Option<C> apply(Option<A> oa, Option<B> ob) {
        Option<Function<B, C>> ofbc = lifted.apply(oa);
        return Options.ap(ob, ofbc);
      }
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
  public static <A, B, C> Function<Function2<A, B, C>, Function2<Option<A>, Option<B>, Option<C>>> lift2() {
    return new Function<Function2<A, B, C>, Function2<Option<A>, Option<B>, Option<C>>>() {
      @Override public Function2<Option<A>, Option<B>, Option<C>> apply(Function2<A, B, C> f2) {
        return lift2(f2);
      }
    };
  }
}

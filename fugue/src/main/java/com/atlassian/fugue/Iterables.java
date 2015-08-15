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

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.atlassian.fugue.Iterators.emptyIterator;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Pair.leftValue;
import static com.atlassian.fugue.Pair.pair;
import static com.atlassian.fugue.Pair.rightValue;
import static com.atlassian.fugue.Suppliers.ofInstance;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

/**
 * Contains static utility methods that operate on or return objects of type
 * {code}Iterable{code}.
 *
 * The iterables produced from the functions in this class are safe to reuse
 * multiple times. Iterables#iterator returns a new iterator each time.
 *
 * @since 1.0
 */
public class Iterables {
  private Iterables() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  static final Iterable<?> EMPTY = new Iterable<Object>() {
    @Override public Iterator<Object> iterator() {
      return emptyIterator();
    }

    @Override public String toString() {
      return "[]";
    }
  };

  /**
   * Returns an empty iterable, that is, an {@code Iterable} with an
   * {@code Iterator} for which {@code hasNext()} always returns {@code false},
   * and the other methods throw appropriate exceptions if called.
   *
   * Intended to be used as a more idiomatic replacement for
   * {@code Collections.emptyList()} in code that otherwise deals only with
   * iterables.
   *
   * @param <T> the type
   * @return an empty iterable
   * @since 1.2
   */
  public static <T> Iterable<T> emptyIterable() {
    @SuppressWarnings("unchecked")
    final Iterable<T> result = (Iterable<T>) EMPTY;
    return result;
  }

  /**
   * Creates an Iterable from the underlying array of elements.
   *
   * @param <A> The type of the elements
   * @param as the elements to iterate over
   * @return an Iterable across the underlying elements
   * @since 2.3
   */
  @SafeVarargs public static <A> Iterable<A> iterable(A... as) {
    return unmodifiableCollection(asList(as));
  }

  /**
   * Finds the first item that matches the predicate. Traditionally, this should
   * be named find; in this case it is named findFirst to avoid clashing with
   * static imports from Guava's com.google.common.collect.Iterables.
   *
   * @param <T> the type
   * @param elements the iterable to search for a matching element
   * @param p the predicate to use to determine if an element is eligible to be
   * returned
   * @return the first item in elements that matches predicate
   * @since 1.0
   */
  public static <T> Option<T> findFirst(final Iterable<? extends T> elements, final Predicate<? super T> p) {
    for (final T t : filter(elements, p)) {
      return some(t);
    }
    return none();
  }

  /**
   * Partial application of the predicate argument to
   * {@link #findFirst(Iterable, Predicate)} returning a function that takes an
   * {@link Iterable} as its argument
   *
   * @param <A> the type
   * @param predicate the predicate to use to determine if an element is
   * eligible to be returned
   * @return a Function that takes an {@link Iterable} as its argument, and
   * returns the first element that satisfies the predicate
   * @since 2.2
   */
  public static <A> Function<Iterable<A>, Option<A>> findFirst(final Predicate<? super A> predicate) {
    return input -> findFirst(input, predicate);
  }

  /**
   * If {@code as} is empty, returns {@code none()}. Otherwise, returns
   * {@code some(get(as, 0))}.
   *
   * @param <A> type of elements in {@code as}
   * @param as elements to get the first value of, must not be null
   * @return {@code none()} if {@code as} is empty. {@code some(get(as, 0))}
   * otherwise
   * @since 1.1
   */
  public static <A> Option<A> first(final Iterable<A> as) {
    for (final A a : as) {
      return some(a);
    }
    return none();
  }

  /**
   * Applies {@code f} to each element of {@code collection}, then concatenates
   * the result.
   *
   * @param <A> type of elements in {@code collection}
   * @param <B> type elements in the new {@code Iterable} {@code f} will
   * transform elements to
   * @param collection elements to apply {@code f} to
   * @param f {@code Function} to apply to elements of {@code collection}
   * @return concatenated result of applying {@code f} to each element of
   * {@code collection}
   * @since 1.1
   */
  public static <A, B> Iterable<B> flatMap(final Iterable<A> collection,
    final Function<? super A, ? extends Iterable<? extends B>> f) {
    return join(map(collection, f));
  }

  /**
   * Applies each function in {@code fs} to {@code arg}.
   *
   * @param <A> the argument type
   * @param <B> the function output and type of the elements of the final
   * iterable.
   * @param fs an iterable of functions that the arg will be applied to
   * @param arg the argument to apply to the functions
   * @return the results of the functions when applied to the arg
   * @since 1.1
   */
  public static <A, B> Iterable<B> revMap(final Iterable<? extends Function<A, B>> fs, final A arg) {
    return map(fs, Functions.<A, B>apply(arg));
  }

  /**
   * Predicate that checks if an iterable is empty.
   *
   * @return {@code Predicate} which checks if an {@code Iterable} is empty
   * @since 1.1
   */
  public static Predicate<Iterable<?>> isEmpty() {
    return it -> {
      if (it instanceof Collection) {
        return ((Collection<?>) it).isEmpty();
      }
      return !it.iterator().hasNext();
    };
  }

  /**
   * Filters and maps (aka transforms) the unfiltered iterable.
   *
   * Applies the given partial function to each element of the unfiltered
   * iterable. If the application returns none, the element will be left out;
   * otherwise, the transformed object contained in the Option will be added to
   * the result.
   *
   * @param <A> the input type
   * @param <B> the output type
   * @param from the input iterable, must not be null and must not contain null
   * @param partial the collecting function
   * @return the collected iterable
   * @since 2.2
   */
  public static <A, B> Iterable<B> collect(Iterable<? extends A> from, Function<? super A, Option<B>> partial) {
    return new CollectingIterable<>(from, partial);
  }

  /**
   * Filter an {@code Iterable} into a {@code Pair} of {@code Iterable}'s.
   *
   * @param <A> the type
   * @param iterable to be filtered
   * @param p to filter each element
   * @return a pair where the left matches the predicate, and the right does
   * not.
   * @since 1.2
   */
  public static <A> Pair<Iterable<A>, Iterable<A>> partition(Iterable<A> iterable, Predicate<? super A> p) {
    return pair(filter(iterable, p), filter(iterable, p.negate()));
  }

  /**
   * Takes the first {@code n} {@code xs} and returns them.
   *
   * @param <T> type of {@code xs}
   * @param n number of {@code xs} to take, must greater than or equal to zero
   * @param xs list of values, must not be null and must not contain null
   * @return first {@code n} {@code xs}
   * @since 1.1
   */
  public static <T> Iterable<T> take(final int n, final Iterable<T> xs) {
    if (n < 0) {
      throw new IllegalArgumentException("Cannot take a negative number of elements");
    }
    if (xs instanceof List<?>) {
      final List<T> list = (List<T>) xs;
      return list.subList(0, n < list.size() ? n : list.size());
    }
    return new Range<>(0, n, xs);
  }

  /**
   * Drop the first {@code n} {@code xs} and return the rest.
   *
   * @param <T> type of {@code xs}
   * @param n number of {@code xs} to drop, must greater than or equal to zero
   * @param xs list of values, must not be null and must not contain null
   * @return remaining {@code xs} after dropping the first {@code n}
   * @since 1.1
   */
  public static <T> Iterable<T> drop(final int n, final Iterable<T> xs) {
    if (n < 0) {
      throw new IllegalArgumentException("Cannot drop a negative number of elements");
    }
    if (xs instanceof List<?>) {
      final List<T> list = (List<T>) xs;
      if (n > (list.size() - 1)) {
        return Collections.emptyList();
      }
      return ((List<T>) xs).subList(n, list.size());
    }
    return new Range<>(n, Integer.MAX_VALUE, xs);
  }

  /**
   * Zips two iterables into a single iterable that produces {@link Pair pairs}.
   * See unzip(Iterable) for the opposite operation
   *
   * @param <A> LHS type
   * @param <B> RHS type
   * @param as left values
   * @param bs right values
   * @return an {@link Iterable iterable} of pairs, only as long as the shortest
   * input iterable.
   *
   *
   * @since 1.2
   */
  public static <A, B> Iterable<Pair<A, B>> zip(final Iterable<A> as, final Iterable<B> bs) {
    return zipWith(Pair.<A, B> pairs()).apply(as, bs);
  }

  /**
   * Takes a two-arg function that returns a third type and reurn a new function
   * that takes iterables of the two input types and combines them into a new
   * iterable.
   *
   * @param <A> LHS type
   * @param <B> RHS type
   * @param <C> result type
   * @param f combiner function, must not be null
   * @return an Function that takes two iterables and zips them using the
   * supplied function. The input iterables must not be null and must not
   * contain null
   * @since 1.2
   */
  public static <A, B, C> BiFunction<Iterable<A>, Iterable<B>, Iterable<C>> zipWith(final BiFunction<A, B, C> f) {
    return (as, bs) -> new Zipper<>(as, bs, f);
  }

  /**
   * Takes an Iterable, and returns an Iterable of a Pair of the original
   * element and its index starting at zero.
   *
   * @param <A> the type
   * @param as the original iterable
   * @return the decorated iterable that generates pairs.
   * @since 1.2
   */
  public static <A> Iterable<Pair<A, Integer>> zipWithIndex(final Iterable<A> as) {
    return zip(as, rangeTo(0, Integer.MAX_VALUE));
  }

  /**
   * Unzips an iterable of {@link Pair pairs} into a {@link Pair pair} of
   * iterables.
   *
   * @param <A> LHS type
   * @param <B> RHS type
   * @param pairs the values
   * @return a {@link Pair pair} of {@link Iterable iterable} of the same length
   * as the input iterable.
   * @since 1.2
   */
  public static <A, B> Pair<Iterable<A>, Iterable<B>> unzip(Iterable<Pair<A, B>> pairs) {
    return pair(map(pairs, leftValue()), map(pairs, rightValue()));
  }

  /**
   * Creates a sequence of {@link Integer integers} from start up to but not
   * including end.
   *
   * @param start from (inclusive)
   * @param end to (exclusive)
   * @return a sequence of {@link Integer integers}
   * @since 1.2
   */
  public static Iterable<Integer> rangeUntil(final int start, final int end) {
    return rangeUntil(start, end, (start > end) ? -1 : 1);
  }

  /**
   * Creates a sequence of {@link Integer integers} from start up to but not
   * including end with the the supplied step between them.
   *
   * @param start from (inclusive)
   * @param end to (exclusive)
   * @param step size to step – must not be zero, must be positive if end is
   * greater than start, neagtive otherwise
   * @return a sequence of {@link Integer integers}
   * @since 1.2
   */
  public static Iterable<Integer> rangeUntil(final int start, final int end, final int step) {
    if (step == 0) {
      throw new IllegalArgumentException("Step must not be zero");
    }
    return rangeTo(start, end - (Math.abs(step) / step), step);
  }

  /**
   * Creates a sequence of {@link Integer integers} from start up to and
   * including end.
   *
   * @param start from (inclusive)
   * @param end to (inclusive)
   * @return a sequence of {@link Integer integers}
   * @since 1.2
   */
  public static Iterable<Integer> rangeTo(final int start, final int end) {
    return rangeTo(start, end, (start > end) ? -1 : 1);
  }

  /**
   * Creates a sequence of {@link Integer integers} from start up to and
   * including end with the the supplied step between them.
   *
   * @param start from (inclusive), must be greater than zero and less than end
   * @param end to (inclusive)
   * @param step size to step – must not be zero, must be positive if end is
   * greater than start, neagtive otherwise
   * @return a sequence of {@link Integer integers}
   * @since 1.2
   */
  public static Iterable<Integer> rangeTo(final int start, final int end, final int step) {
    if (step == 0) {
      throw new IllegalArgumentException("Step must not be zero");
    }
    if (step > 0) {
      if (start > end) {
        throw new IllegalArgumentException(String.format("Start %s must not be greater than end %s with step %s",
          start, end, step));
      }
    } else {
      if (start < end) {
        throw new IllegalArgumentException(String.format("Start %s must not be less than end %s with step %s", start,
          end, step));
      }
    }

    return () -> new Iterators.Unmodifiable<Integer>() {
      private int i = start;

      @Override public boolean hasNext() {
        return step > 0 ? i <= end : i >= end;
      }

      @Override public Integer next() {
        try {
          return i;
        } finally {
          i += step;
        }
      }
    };
  }

  static abstract class IterableToString<A> implements Iterable<A> {
    @Override public final String toString() {
      final Iterator<A> it = this.iterator();
      final StringBuilder buffer = new StringBuilder().append("[");
      while (it.hasNext()) {
        buffer.append(Objects.requireNonNull(it.next()).toString());
        if (it.hasNext()) {
          buffer.append(", ");
        }
      }
      buffer.append("]");
      return buffer.toString();
    }
  }

  /**
   * Iterable that only shows a small range of the original Iterable.
   */
  static final class Range<A> extends IterableToString<A> {
    private final Iterable<A> delegate;
    private final int drop;
    private final int size;

    private Range(final int drop, final int size, final Iterable<A> delegate) {
      this.delegate = requireNonNull(delegate);
      this.drop = drop;
      this.size = size;
    }

    @Override public Iterator<A> iterator() {
      return new Iter<>(drop, size, delegate.iterator());
    }

    static final class Iter<T> extends Iterators.Abstract<T> {
      private final Iterator<T> it;
      private int remaining;

      Iter(final int drop, final int size, final Iterator<T> it) {
        this.it = it;
        this.remaining = size;

        for (int i = 0; (i < drop) && it.hasNext(); i++) {
          it.next();
        }
      }

      @Override protected T computeNext() {
        if ((remaining > 0) && it.hasNext()) {
          remaining--;
          return it.next();
        } else {
          return endOfData();
        }
      }
    }
  }

  /**
   * CollectingIterable, filters and transforms in one.
   */
  static class CollectingIterable<A, B> extends IterableToString<B> {
    private final Iterable<? extends A> delegate;
    private final Function<? super A, Option<B>> partial;

    CollectingIterable(Iterable<? extends A> delegate, Function<? super A, Option<B>> partial) {
      this.delegate = requireNonNull(delegate);
      this.partial = requireNonNull(partial);
    }

    @Override public Iterator<B> iterator() {
      return new Iter();
    }

    final class Iter extends Iterators.Abstract<B> {
      private final Iterator<? extends A> it = delegate.iterator();

      @Override protected B computeNext() {
        while (it.hasNext()) {
          final Option<B> result = partial.apply(it.next());
          if (result.isDefined())
            return result.get();
        }
        return endOfData();
      }
    }
  }

  /**
   * Iterable that combines two iterables using a combiner function.
   */
  static class Zipper<A, B, C> extends IterableToString<C> {
    private final Iterable<A> as;
    private final Iterable<B> bs;
    private final BiFunction<A, B, C> f;

    Zipper(final Iterable<A> as, final Iterable<B> bs, final BiFunction<A, B, C> f) {
      this.as = requireNonNull(as, "as must not be null.");
      this.bs = requireNonNull(bs, "bs must not be null.");
      this.f = requireNonNull(f, "f must not be null.");
    }

    @Override public Iterator<C> iterator() {
      return new Iter();
    }

    class Iter implements Iterator<C> {
      private final Iterator<A> a = requireNonNull(as.iterator(), "as iterator must not be null.");
      private final Iterator<B> b = requireNonNull(bs.iterator(), "bs iterator must not be null.");

      @Override public boolean hasNext() {
        return a.hasNext() && b.hasNext();
      }

      @Override public C next() {
        return f.apply(a.next(), b.next());
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }

  /**
   * Intersperse an element between all the elements in an iterable.
   *
   * @param <A> the type of the elements.
   * @param as the source iterable.
   * @param a the element to intersperse between the source elements.
   * @return a new Iterable that intersperses the element between the source.
   * @since 2.3
   */
  public static <A> Iterable<A> intersperse(final Iterable<? extends A> as, A a) {
    return intersperse(as, ofInstance(a));
  }

  /**
   * Intersperse an element between all the elements in an iterable.
   *
   * @param <A> the type of the elements.
   * @param as the source iterable.
   * @param a the supplier of elements to intersperse between the source
   * elements.
   * @return a new Iterable that intersperses the element between the source.
   * @since 2.3
   */
  public static <A> Iterable<A> intersperse(final Iterable<? extends A> as, Supplier<A> a) {
    return new Intersperse<>(as, a);
  }

  static final class Intersperse<A> implements Iterable<A> {
    private final Iterable<? extends A> as;
    private final Supplier<A> a;

    Intersperse(Iterable<? extends A> as, Supplier<A> a) {
      this.as = as;
      this.a = a;
    }

    @Override public Iterator<A> iterator() {
      return new Iterators.Abstract<A>() {
        private final Iterator<? extends A> it = as.iterator();
        private boolean inter = false;

        @Override protected A computeNext() {
          if (!it.hasNext()) {
            return endOfData();
          }
          try {
            return inter ? a.get() : it.next();
          } finally {
            inter = !inter;
          }
        }
      };
    }
  }

  /**
   * Return the size of an iterable. In most cases this function is required to
   * walk the entire iterable to determine the result. Consider this an O(n)
   * complexity function.
   *
   * @param as iterable to compute the size of
   * @param <A> element type
   * @return number of elements in the iterable
   *
   * @since 3.0
   */
  public static <A> int size(Iterable<A> as) {
    if (as instanceof Collection) {
      return ((Collection<?>) as).size();
    } else {
      final Iterator<A> iterator = as.iterator();
      int count = 0;
      while (iterator.hasNext()) {
        iterator.next();
        count++;
      }
      return count;
    }
  }

  /**
   * Transform an interable by mapping a function across each of its elements
   *
   * @param as the source iterable
   * @param f function to apply to all the elements of as
   * @param <A> original iterable type
   * @param <B> output iterable type
   * @return new iterable containing the transformed values produced by f#apply
   * @since 3.0
   * @deprecated function provided to make migration easier prefer to use #map where possible
   */
  @Deprecated
  public static <A, B> Iterable<B> transform(final Iterable<A> as, final Function<? super A, ? extends B> f) {
    return map(as,f);
  }

  /**
   * Apply the input function to each of the elements of the input iterable returning a new iterable
   *
   * @param as the source iterable
   * @param f function to apply to all the elements of as
   * @param <A> original iterable type
   * @param <B> output iterable type
   * @return new iterable containing values produced by f#apply called on each element
   * @since 3.0
   */
  public static <A, B> Iterable<B> map(final Iterable<A> as, final Function<? super A, ? extends B> f) {
    return new Mapped<>(as, f);
  }


  static final class Mapped<A, B> implements Iterable<B> {
    private final Iterable<? extends A> as;
    private final Function<? super A, ? extends B> f;

    Mapped(Iterable<? extends A> as, Function<? super A, ? extends B> f) {
      this.as = as;
      this.f = f;
    }

    @Override public Iterator<B> iterator() {
      return new Iterators.Abstract<B>() {
        private final Iterator<? extends A> it = as.iterator();

        @Override protected B computeNext() {
          if (!it.hasNext()) {
            return endOfData();
          }
          return f.apply(it.next());
        }
      };
    }
  }

  /**
   * Remove elements from the input iterable for which the predicate returns
   * false
   *
   * @param as original iterable
   * @param p predicate to filter by
   * @param <A> element type
   * @return new iterable containing only those elements for which p#test
   * returns true
   *
   * @since 3.0
   */
  public static <A> Iterable<A> filter(final Iterable<A> as, final Predicate<? super A> p) {
    return new Filter<>(as, p);
  }

  static final class Filter<A> implements Iterable<A> {
    private final Iterable<? extends A> as;
    private final Predicate<? super A> p;

    Filter(Iterable<? extends A> as, Predicate<? super A> p) {
      this.as = as;
      this.p = p;
    }

    @Override public Iterator<A> iterator() {
      return new Iterators.Abstract<A>() {
        private final Iterator<? extends A> it = as.iterator();

        @Override protected A computeNext() {
          if (!it.hasNext()) {
            return endOfData();
          }
          while (it.hasNext()) {
            final A a = it.next();
            if (p.test(a)) {
              return a;
            }
          }
          return endOfData();
        }
      };
    }
  }

  /**
   * Join {@literal Iterable<Iterable<A>>} down to {@literal Iterable<A>}. The
   * resulting iterable will exhaust the first input iterable in order before
   * returning values from the second. Input iterables must not be null and
   * must not contain null.
   *
   * @param ias one or more iterable to merge into the final iterable result,
   * must not be null and must not return null
   * @param <A> element type
   * @return single level iterable with all the elements of the original
   * iterables
   *
   * @since 3.0
   */
  public static <A> Iterable<A> join(Iterable<? extends Iterable<? extends A>> ias) {
    return new Join<>(ias);
  }

  static final class Join<A> extends IterableToString<A> {
    private final Iterable<? extends Iterable<? extends A>> ias;

    public Join(Iterable<? extends Iterable<? extends A>> ias) {
      this.ias = ias;
    }

    @Override public Iterator<A> iterator() {
      return new Iter<>(ias);
    }

    static class Iter<A> extends Iterators.Abstract<A> {
      final Queue<Iterator<? extends A>> qas;

      public Iter(final Iterable<? extends Iterable<? extends A>> ias)
      {
        qas = new LinkedList<>();
        for (Iterable<? extends A> a : ias) {
          Iterator<? extends A> as = requireNonNull(a.iterator());
          qas.add(as);
        }
      }

      @Override protected A computeNext() {
        while(!qas.isEmpty() && !qas.peek().hasNext()){
          qas.remove();
        }
        if(qas.isEmpty()){
          return endOfData();
        }
        return qas.peek().next();
      }
    }
  }

  /**
   * Concatenate a series of iterables into a single iterable. Returns an empty
   * iterable if no iterables are supplied. Input iterables must not be null
   * and must not contain null.
   *
   * @param as any number of iterables containing A
   * @param <A> super type of contained by all input iterables
   * @return new iterable containing all the elements of the input iterables
   *
   * @since 3.0
   */
  @SafeVarargs public static <A> Iterable<A> concat(Iterable<? extends A> ...as){
    return as.length > 0 ? join(Arrays.asList(as)) : emptyIterable();
  }

  /**
   * Check if the iterable contains any elements that match the predicate.
   *
   * @param as iterable to compare for matching elements
   * @param p predicate to test for matching elements
   * @param <A> type of elements inside the input iterable
   * @return true if any element in the iterable returns true for the input
   * predicate otherwise false. False for an empty iterable.
   * @since 3.0
   */
  public static <A> boolean any(final Iterable<? extends A> as, final Predicate<? super A> p) {
    return !isEmpty().test(filter(as, p));
  }

  /**
   * Check if all elements in the input iterable match the input predicate
   *
   * @param as iterable to compare for matching elements
   * @param p predicate to test for matching elements
   * @param <A> type of elements inside the input iterable
   * @return true if all elements in the iterable return true for the input
   * predicate otherwise false. True for an empty iterable.
   * @since 3.0
   */
  public static <A> boolean all(final Iterable<? extends A> as, final Predicate<? super A> p) {
    return isEmpty().test(filter(as, p.negate()));
  }

  /**
   * Returns an infinite Iterable constructed by applying the given iteration
   * function starting at the given value.
   *
   * @param <A> type of the elements
   * @param f The iteration function, must not return null
   * @param start The value to begin iterating from.
   * @return An infinite Iterable of repeated applications of {@code f} to
   * {@code start}.
   * @since 2.4
   */
  public static <A> Iterable<A> iterate(final Function<? super A, ? extends A> f, final A start) {
    return new IteratingIterable<>(f, start);
  }

  /**
   * Infinite iterable that repeatedly applies {@code f} to {@code start}
   */
  static final class IteratingIterable<A> implements Iterable<A> {
    private final Function<? super A, ? extends A> f;
    private final A start;

    private IteratingIterable(final Function<? super A, ? extends A> f, final A start) {
      this.f = requireNonNull(f, "f");
      this.start = start;
    }

    @Override public Iterator<A> iterator() {
      return new Iter<>(f, start);
    }

    static final class Iter<A> extends Iterators.Unmodifiable<A> {
      private final Function<? super A, ? extends A> f;
      private A current;

      Iter(final Function<? super A, ? extends A> f, final A start) {
        this.f = f;
        this.current = start;
      }

      @Override public boolean hasNext() {
        return true;
      }

      @Override public A next() {
        final A value = current;
        current = f.apply(current);
        return value;
      }
    }
  }

  /**
   * Builds an Iterable from a seed value until {@code f} returns {@code none()}
   * .
   *
   * @param <A> type of the returned elements.
   * @param <B> type of the elements for which {@code f} is applied.
   * @param f The function that returns some(pair(a, b)), in which case
   * {@code a} is the next element of the resulting Iterable and {@code b} is
   * used as the input value for the next call of {@code f}, or {@code none()}
   * if it is done producing the elements. f must not be null and must not
   * return a pair containing null.
   * @param seed The start value to begin the unfold.
   * @return An Iterable that is a result of unfolding.
   * @since 2.4
   */
  public static <A, B> Iterable<A> unfold(final Function<? super B, Option<Pair<A, B>>> f, final B seed) {
    return new UnfoldingIterable<>(f, seed);
  }

  /**
   * Iterable that repeatedly applies {@code f} until it returns {@code none()}
   */
  static final class UnfoldingIterable<A, B> extends IterableToString<A> {
    private final Function<? super B, Option<Pair<A, B>>> f;
    private final B seed;

    private UnfoldingIterable(final Function<? super B, Option<Pair<A, B>>> f, final B seed) {
      this.f = requireNonNull(f, "f");
      this.seed = seed;
    }

    @Override public Iterator<A> iterator() {
      return new Iter<A, B>(f, seed);
    }

    static final class Iter<A, B> extends Iterators.Abstract<A> {
      private final Function<? super B, Option<Pair<A, B>>> f;
      private B current;

      Iter(final Function<? super B, Option<Pair<A, B>>> f, final B seed) {
        this.f = f;
        this.current = seed;
      }

      @Override protected A computeNext() {
        final Option<Pair<A, B>> option = f.apply(current);
        if (option.isDefined()) {
          final Pair<A, B> pair = option.get();
          current = pair.right();
          return pair.left();
        } else {
          return endOfData();
        }
      }
    }
  }

  /**
   * Merge a number of already sorted collections of elements into a single
   * collection of elements, using the elements natural ordering.
   *
   * @param <A> type of the elements
   * @param xss collection of already sorted collections, must not be null and
   * must not return null
   * @return {@code xss} merged in a sorted order
   * @since 1.1
   */
  public static <A extends Comparable<A>> Iterable<A> mergeSorted(final Iterable<? extends Iterable<A>> xss) {
    return mergeSorted(xss, Comparator.<A> naturalOrder());
  }

  /**
   * Merge a number of already sorted collections of elements into a single
   * collection of elements.
   *
   * @param <A> type of the elements
   * @param xss already sorted collection of collections, must not be null and
   * must not return null
   * @param ordering ordering to use when comparing elements, must not be null
   * @return {@code xss} merged in a sorted order
   * @since 1.1
   */
  public static <A> Iterable<A> mergeSorted(final Iterable<? extends Iterable<A>> xss, final Comparator<A> ordering) {
    return new MergeSortedIterable<>(xss, ordering);
  }

  /**
   * Add all the elements of the iterable to the collection
   *
   * @param collectionToModify collection to add elements to
   * @param elementsToAdd source of addtional elements
   * @param <A> element type
   * @return true if the collectionToModify was changed
   *
   * @since 3.0
   */
  public static <A> boolean addAll(Collection<A> collectionToModify, Iterable<? extends A> elementsToAdd) {
    if (elementsToAdd instanceof Collection) {
      return collectionToModify.addAll((Collection<? extends A>) elementsToAdd);
    }
    return Iterators.addAll(collectionToModify, requireNonNull(elementsToAdd).iterator());
  }

  /**
   * Merges multiple sorted Iterables into one, sorted iterable.
   */
  static final class MergeSortedIterable<A> extends IterableToString<A> {
    private final Iterable<? extends Iterable<A>> xss;
    private final Comparator<A> comparator;

    MergeSortedIterable(final Iterable<? extends Iterable<A>> xss, final Comparator<A> comparator) {
      this.xss = requireNonNull(xss, "xss");
      this.comparator = requireNonNull(comparator, "comparator");
    }

    @Override public Iterator<A> iterator() {
      return new Iter<>(xss, comparator);
    }

    private static final class Iter<A> extends Iterators.Abstract<A> {
      private final TreeSet<Iterators.Peeking<A>> xss;

      private Iter(final Iterable<? extends Iterable<A>> xss, final Comparator<A> c) {
        this.xss = new TreeSet<>(peekingIteratorComparator(c));
        addAll(this.xss, map(filter(xss, isEmpty().negate()), i -> Iterators.peekingIterator(i.iterator())));
      }

      @Override protected A computeNext() {
        final Option<Iterators.Peeking<A>> currFirstOption = first(xss);
        if (!currFirstOption.isDefined()) {
          return endOfData();
        }
        final Iterators.Peeking<A> currFirst = currFirstOption.get();

        // We remove the iterator from the set first, before we mutate it,
        // otherwise we wouldn't be able to
        // properly find it to remove it. Mutation sucks.
        xss.remove(currFirst);

        final A next = currFirst.next();
        if (currFirst.hasNext()) {
          xss.add(currFirst);
        }
        return next;
      }

      private Comparator<? super Iterators.Peeking<A>> peekingIteratorComparator(final Comparator<A> comparator) {
        return (lhs, rhs) -> (lhs == rhs) ? 0 : comparator.compare(lhs.peek(), rhs.peek());
      }
    }
  }

  /**
   * Makes a lazy copy of {@code xs}.
   *
   * @param <A> type of elements in {@code xs}
   * @param xs {@code Iterable} to be memoized
   * @return lazy copy of {@code as}
   * @since 1.1
   */
  public static <A> Iterable<A> memoize(final Iterable<A> xs) {
    return new Memoizer<>(xs);
  }

  /**
   * Memoizing iterable, maintains a lazily computed linked list of nodes.
   */
  static final class Memoizer<A> extends IterableToString<A> {
    private final Node<A> head;

    Memoizer(final Iterable<A> delegate) {
      head = nextNode(delegate.iterator());
    }

    @Override public Iterator<A> iterator() {
      return new Iter<>(head);
    }

    private static <A> Node<A> nextNode(final Iterator<A> delegate) {
      return delegate.hasNext() ? new Lazy<>(delegate) : new End<>();
    }

    /**
     * Linked list node.
     */
    interface Node<A> {
      boolean isEnd();

      A value();

      /**
       * Get the next Node.
       *
       * @return a new Node
       * @throws java.util.NoSuchElementException if this is terminal
       */
      Node<A> next() throws NoSuchElementException;
    }

    /**
     * Lazily computes the next node. Has a value so is not an end.
     */
    static class Lazy<A> extends LazyReference<Node<A>> implements Node<A> {
      private final Iterator<A> delegate;
      private final A value;

      Lazy(final Iterator<A> delegate) {
        this.delegate = delegate;
        this.value = delegate.next();
      }

      @Override protected Node<A> create() throws Exception {
        return nextNode(delegate);
      }

      @Override public Node<A> next() throws NoSuchElementException {
        return get();
      }

      @Override public boolean isEnd() {
        return false;
      }

      @Override public A value() {
        return value;
      }
    }

    static class End<A> implements Node<A> {
      @Override public boolean isEnd() {
        return true;
      }

      // /CLOVER:OFF
      @Override public Node<A> next() {
        throw new NoSuchElementException();
      }

      @Override public A value() {
        throw new NoSuchElementException();
      }
      // /CLOVER:ON
    }

    static class Iter<A> extends Iterators.Abstract<A> {
      Node<A> node;

      Iter(final Node<A> node) {
        this.node = node;
      }

      @Override protected A computeNext() {
        if (node.isEnd()) {
          return endOfData();
        }
        try {
          return node.value();
        } finally {
          node = node.next();
        }
      }
    }
  }

  /**
   * Class supports the implementation of {@link Iterables#memoize(Iterable)}
   * and is not intended for general use.
   *
   * Lazily loaded reference that is not constructed until required. This class
   * is used to maintain a reference to an object that is expensive to create
   * and must be constructed once and once only. This reference behaves as
   * though the <code>final</code> keyword has been used (you cannot reset it
   * once it has been constructed). Object creation is guaranteed to be
   * thread-safe and the first thread that calls {@link #get()} will be the one
   * that creates it.
   * <p>
   * Usage: clients need to implement the {@link #create()} method to return the
   * object this reference will hold.
   * <p>
   * For instance:
   * <p>
   *
   * <pre>
   * final LazyReference&lt;MyObject&gt; ref = new LazyReference() {
   *   protected MyObject create() throws Exception {
   *     // Do expensive object construction here
   *     return new MyObject();
   *   }
   * };
   * </pre>
   *
   * Then call {@link #get()} to get a reference to the referenced object:
   *
   * <pre>
   * MyObject myLazyLoadedObject = ref.get()
   * </pre>
   *
   * NOTE: Interruption policy is that if you want to be cancellable while
   * waiting for another thread to create the value, instead of calling
   * {@link #get()} call {@link #getInterruptibly()}. However, If your
   * {@link #create()} method is interrupted and throws an
   * {@link InterruptedException}, it is treated as an application exception and
   * will be the causal exception inside the runtime
   * {@link InitializationException} that {@link #get()} or
   * {@link #getInterruptibly()} throws and your {@link #create()} will not be
   * called again.
   * <p>
   * This class is NOT {@link Serializable}.
   * <p>
   * Implementation note. This class extends {@link WeakReference} as
   * {@link Reference} does not have a public constructor. WeakReference is
   * preferable as it does not have any members and therefore doesn't increase
   * the memory footprint. As we never pass a referent through to the
   * super-class and override {@link #get()}, the garbage collection semantics
   * of WeakReference are irrelevant. The referenced object will not become
   * eligible for GC unless the object holding the reference to this object is
   * collectible.
   *
   * @param <T> the type of the contained element.
   */
  // @ThreadSafe
  static abstract class LazyReference<T> extends WeakReference<T> implements Supplier<T> {

    private final Sync sync = new Sync();

    public LazyReference() {
      super(null);
    }

    /**
     * The object factory method, guaranteed to be called once and only once.
     *
     * @return the object that {@link #get()} and {@link #getInterruptibly()}
     * will return.
     * @throws Exception if anything goes wrong, rethrown as an
     * InitializationException from {@link #get()} and
     * {@link #getInterruptibly()}
     */
    protected abstract T create() throws Exception;

    /**
     * Get the lazily loaded reference in a non-cancellable manner. If your
     * <code>create()</code> method throws an Exception calls to
     * <code>get()</code> will throw an InitializationException which wraps the
     * previously thrown exception.
     *
     * @return the object that {@link #create()} created.
     * @throws InitializationException if the {@link #create()} method throws an
     * exception. The {@link InitializationException#getCause()} will contain
     * the exception thrown by the {@link #create()} method
     */
    @Override public final T get() {
      boolean interrupted = false;
      try {
        while (true) {
          try {
            return getInterruptibly();
          } catch (final InterruptedException ignore) {
            // ignore and try again
            interrupted = true;
          }
        }
      } finally {
        if (interrupted) {
          Thread.currentThread().interrupt();
        }
      }
    }

    /**
     * Get the lazily loaded reference in a cancellable manner. If your
     * <code>create()</code> method throws an Exception, calls to
     * <code>get()</code> will throw a RuntimeException which wraps the
     * previously thrown exception.
     *
     * @return the object that {@link #create()} created.
     * @throws InitializationException if the {@link #create()} method throws an
     * exception. The {@link InitializationException#getCause()} will contain
     * the exception thrown by the {@link #create()} method
     * @throws InterruptedException If the calling thread is Interrupted while
     * waiting for another thread to create the value (if the creating thread is
     * interrupted while blocking on something, the {@link InterruptedException}
     * will be thrown as the causal exception of the
     * {@link InitializationException} to everybody calling this method).
     */
    public final T getInterruptibly() throws InterruptedException {
      if (!sync.isDone()) {
        sync.run();
      }

      try {
        return sync.get();
      } catch (final ExecutionException e) {
        throw new InitializationException(e);
      }
    }

    /**
     * Has the {@link #create()} reference been initialized.
     *
     * @return true if the task is complete
     */
    public final boolean isInitialized() {
      return sync.isDone();
    }

    /**
     * Cancel the initializing operation if it has not already run. Will try and
     * interrupt if it is currently running.
     */
    public final void cancel() {
      sync.cancel(true);
    }

    /**
     * If the factory {@link LazyReference#create()} method threw an exception,
     * this wraps it.
     */
    public static class InitializationException extends RuntimeException {
      private static final long serialVersionUID = 3638376010285456759L;

      InitializationException(final ExecutionException e) {
        super((e.getCause() != null) ? e.getCause() : e);
      }
    }

    static final class State {
      static final int INIT = 0;
      static final int RUNNING = 1;
      static final int RAN = 2;
      static final int CANCELLED = 4;
    }

    /**
     * Synchronization control for LazyReference. Note that this must be a
     * non-static inner class in order to invoke the protected <tt>create</tt>
     * method. Taken from FutureTask AQS implementation and pruned to be as
     * compact as possible.
     *
     * Uses AQS sync state to represent run status.
     */
    private final class Sync extends AbstractQueuedSynchronizer {

      static final int IGNORED = 0;

      /**
       * only here to shut up the compiler warnings, the outer class is NOT
       * serializable
       */
      private static final long serialVersionUID = -1645412544240373524L;

      /** The result to return from get() */
      private T result;
      /** The exception to throw from get() */
      private Throwable exception;

      /**
       * The thread running task. When nulled after set/cancel, this indicates
       * that the results are accessible. Must be volatile, to ensure visibility
       * upon completion.
       */
      private volatile Thread runner;

      private boolean ranOrCancelled(final int state) {
        return (state & (State.RAN | State.CANCELLED)) != State.INIT;
      }

      /**
       * Implements AQS base acquire to succeed if ran or cancelled
       */
      @Override protected int tryAcquireShared(final int ignore) {
        return isDone() ? 1 : -1;
      }

      /**
       * Implements AQS base release to always signal after setting final done
       * status by nulling runner thread.
       */
      @Override protected boolean tryReleaseShared(final int ignore) {
        runner = null;
        return true;
      }

      boolean isDone() {
        return ranOrCancelled(getState()) && (runner == null);
      }

      T get() throws InterruptedException, ExecutionException {
        acquireSharedInterruptibly(IGNORED);
        if (getState() == State.CANCELLED) {
          throw new CancellationException();
        }
        if (exception != null) {
          throw new ExecutionException(exception);
        }
        return result;
      }

      void set(final T v) {
        for (;;) {
          final int s = getState();
          if (s == State.RAN) {
            return;
          }
          if (s == State.CANCELLED) {
            // aggressively release to set runner to null,
            // in case we are racing with a cancel request
            // that will try to interrupt runner
            releaseShared(IGNORED);
            return;
          }
          if (compareAndSetState(s, State.RAN)) {
            result = v;
            releaseShared(IGNORED);
            return;
          }
        }
      }

      void setException(final Throwable t) {
        for (;;) {
          final int s = getState();
          if (s == State.RAN) {
            return;
          }
          if (s == State.CANCELLED) {
            // aggressively release to set runner to null,
            // in case we are racing with a cancel request
            // that will try to interrupt runner
            releaseShared(0);
            return;
          }
          if (compareAndSetState(s, State.RAN)) {
            exception = t;
            result = null;
            releaseShared(0);
            return;
          }
        }
      }

      void cancel(final boolean mayInterruptIfRunning) {
        for (;;) {
          final int s = getState();
          if (ranOrCancelled(s)) {
            return;
          }
          if (compareAndSetState(s, State.CANCELLED)) {
            break;
          }
        }
        if (mayInterruptIfRunning) {
          final Thread r = runner;
          if (r != null) {
            r.interrupt();
          }
        }
        releaseShared(IGNORED);
      }

      void run() {
        if ((getState() != State.INIT) || !compareAndSetState(State.INIT, State.RUNNING)) {
          if (runner == Thread.currentThread()) {
            throw new IllegalMonitorStateException("Not reentrant!");
          }
          return;
        }
        try {
          runner = Thread.currentThread();
          set(create());
        } catch (final Throwable ex) {
          setException(ex);
        }
      }
    }
  }
}

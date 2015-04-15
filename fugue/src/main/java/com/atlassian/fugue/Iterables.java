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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.atlassian.fugue.Iterators.peekingIterator;
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
 * This class is primarily focused around filling holes in Guava's Iterables
 * class which have become apparent with the addition of Fugue classes such as
 * Option and Either.
 *
 * When making changes to this class, please try to name methods differently to
 * those in Iterables so that methods from both classes can be statically
 * imported in the same class.
 *
 * @since 1.0
 */
public class Iterables {
  private Iterables() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  static final Iterable<?> EMPTY = new Iterable<Object>() {
    @Override public Iterator<Object> iterator() {
      return Functions.emptyIterator();
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
    Iterable<T> result = (Iterable<T>) EMPTY;
    return result;
  }

  /**
   * Creates an Iterable from the underlying array of elements.
   *
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
    for (final T t : Iterables.filter(elements, p)) {
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
   * @param as elements to get the first value of
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
    return flatten(Iterables.transform(collection, f));
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
    return Iterables.transform(fs, Functions.<A, B> apply(arg));
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
   * @param from the input iterable
   * @param partial the collecting function
   * @return the collected iterable
   */
  public static <A, B> Iterable<B> collect(Iterable<? extends A> from, Function<? super A, Option<B>> partial) {
    return new CollectingIterable<>(from, partial);
  }

  /**
   * Filter an {@code Iterable} into a {@code Pair} of {@code Iterable}'s.
   *
   * @param <A> the type
   * @param iterable to be filtered
   * @param pred to filter each element
   * @return a pair where the left matches the predicate, and the right does
   * not.
   */
  public static <A> Pair<Iterable<A>, Iterable<A>> partition(Iterable<A> iterable, Predicate<? super A> p) {
    return pair(filter(iterable, p), filter(iterable, p.negate()));
  }

  /**
   * Takes the first {@code n} {@code xs} and returns them.
   *
   * @param <T> type of {@code xs}
   * @param n number of {@code xs} to take
   * @param xs list of values
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
   * @param n number of {@code xs} to drop
   * @param xs list of values
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
        return Collections.nCopies(0, (T) null);
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
   * @param f combiner function
   * @return an Function that takes two iterables and zips them using the
   * supplied function
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
   * @since 2.2
   */
  public static <A, B> Pair<Iterable<A>, Iterable<B>> unzip(Iterable<Pair<A, B>> pairs) {
    return pair(transform(pairs, leftValue()), transform(pairs, rightValue()));
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
   * @param start from (inclusive)
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

    return () -> new UnmodifiableIterator<Integer>() {
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
      Iterator<A> it = this.iterator();
      StringBuilder buffer = new StringBuilder().append("[");
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

    public Iterator<A> iterator() {
      return new Iter<>(drop, size, delegate.iterator());
    }

    static final class Iter<T> extends AbstractIterator<T> {
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

    public Iterator<B> iterator() {
      return new Iter();
    }

    final class Iter extends AbstractIterator<B> {
      private final Iterator<? extends A> it = delegate.iterator();

      @Override protected B computeNext() {
        while (it.hasNext()) {
          Option<B> result = partial.apply(it.next());
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
      return new AbstractIterator<A>() {
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

  static <A> int size(Iterable<A> as) {
    if (as instanceof Collection) {
      return ((Collection<?>) as).size();
    } else {
      Iterator<A> iterator = as.iterator();
      int count = 0;
      while (iterator.hasNext()) {
        iterator.next();
        count++;
      }
      return count;
    }
  }

  public static <A, B> Iterable<B> transform(final Iterable<A> as, final Function<? super A, ? extends B> f) {
    return new Transform<>(as, f);
  }

  static final class Transform<A, B> implements Iterable<B> {
    private final Iterable<? extends A> as;
    private final Function<? super A, ? extends B> f;

    Transform(Iterable<? extends A> as, Function<? super A, ? extends B> f) {
      this.as = as;
      this.f = f;
    }

    @Override public Iterator<B> iterator() {
      return new AbstractIterator<B>() {
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
      return new AbstractIterator<A>() {
        private final Iterator<? extends A> it = as.iterator();

        @Override protected A computeNext() {
          if (!it.hasNext()) {
            return endOfData();
          }
          while (it.hasNext()) {
            A a = it.next();
            if (p.test(a)) {
              return a;
            }
          }
          return endOfData();
        }
      };
    }
  }

  public static <A> Iterable<A> flatten(Iterable<? extends Iterable<? extends A>> ias) {
    return new Flatten<>(ias);
  }

  static final class Flatten<A> implements Iterable<A> {
    private final Iterable<? extends Iterable<? extends A>> ias;

    public Flatten(Iterable<? extends Iterable<? extends A>> ias) {
      this.ias = ias;
    }

    @Override public Iterator<A> iterator() {
      return new AbstractIterator<A>() {
        private final Iterator<? extends Iterable<? extends A>> i = ias.iterator();
        private Iterator<? extends A> currentIterator = Functions.emptyIterator();

        @Override protected A computeNext() {
          boolean currentHasNext;
          while (!(currentHasNext = Objects.requireNonNull(currentIterator).hasNext()) && i.hasNext()) {
            currentIterator = i.next().iterator();
          }
          if (!currentHasNext) {
            return endOfData();
          }
          return currentIterator.next();
        }
      };
    }
  }

  /**
   * Merge a number of already sorted collections of elements into a single
   * collection of elements, using the elements natural ordering.
   *
   * @param <A> type of the elements
   * @param xss collection of already sorted collections
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
   * @param xss already sorted collection of collections
   * @param ordering ordering to use when comparing elements
   * @return {@code xss} merged in a sorted order
   * @since 1.1
   */
  public static <A> Iterable<A> mergeSorted(final Iterable<? extends Iterable<A>> xss, final Comparator<A> ordering) {
    return new MergeSortedIterable<>(xss, ordering);
  }

  public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
    if (elementsToAdd instanceof Collection) {
      Collection<? extends T> c = (Collection<? extends T>) elementsToAdd;
      return addTo.addAll(c);
    }
    return Iterators.addAll(addTo, requireNonNull(elementsToAdd).iterator());
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

    public Iterator<A> iterator() {
      return new Iter<>(xss, comparator);
    }

    private static final class Iter<A> extends AbstractIterator<A> {
      private final TreeSet<PeekingIterator<A>> xss;

      private Iter(final Iterable<? extends Iterable<A>> xss, final Comparator<A> c) {
        this.xss = new TreeSet<>(peekingIteratorComparator(c));
        addAll(this.xss, transform(filter(xss, isEmpty().negate()), i -> peekingIterator(i.iterator())));
      }

      @Override protected A computeNext() {
        final Option<PeekingIterator<A>> currFirstOption = first(xss);
        if (!currFirstOption.isDefined()) {
          return endOfData();
        }
        final PeekingIterator<A> currFirst = currFirstOption.get();

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

      private Comparator<? super PeekingIterator<A>> peekingIteratorComparator(final Comparator<A> comparator) {
        return (lhs, rhs) -> (lhs == rhs) ? 0 : comparator.compare(lhs.peek(), rhs.peek());
      }
    }
  }
}
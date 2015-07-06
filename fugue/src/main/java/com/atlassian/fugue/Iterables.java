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

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.atlassian.fugue.Pair.pair;
import static com.atlassian.fugue.Suppliers.ofInstance;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Contains static utility methods that operate on or return objects of type
 * {code}Iterable{code}.
 * 
 * This class is primarily focused around filling holes in Guava's
 * {@link com.google.common.collect.Iterables} class which have become apparent
 * with the addition of Fugue classes such as Option and Either.
 * 
 * When making changes to this class, please try to name methods differently to
 * those in {@link com.google.common.collect.Iterables} so that methods from
 * both classes can be statically imported in the same class.
 * 
 * @since 1.0
 */
public class Iterables {
  private Iterables() {
    throw new UnsupportedOperationException("This class is not instantiable.");
  }

  static final Iterable<?> EMPTY = new Iterable<Object>() {
    @Override public Iterator<Object> iterator() {
      return Iterators.emptyIterator();
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
   * @param <A> The type of the elements
   * @param as the elements to iterate over
   * @return an Iterable across the underlying elements
   * @since 2.3
   */
  public static <A> Iterable<A> iterable(A... as) {
    return unmodifiableCollection(asList(as));
  }

  /**
   * Finds the first item that matches the predicate. Traditionally, this should
   * be named find; in this case it is named findFirst to avoid clashing with
   * static imports from Guava's {@link com.google.common.collect.Iterables}.
   * 
   * @param <T> the type
   * @param elements the iterable to search for a matching element
   * @param predicate the predicate to use to determine if an element is
   * eligible to be returned
   * @return the first item in elements that matches predicate
   * @since 1.0
   */
  public static <T> Option<T> findFirst(final Iterable<? extends T> elements, final Predicate<? super T> predicate) {
    for (final T t : filter(elements, predicate)) {
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
    return new Function<Iterable<A>, Option<A>>() {
      @Override public Option<A> apply(Iterable<A> input) {
        return findFirst(input, predicate);
      }
    };
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
    return concat(transform(collection, f));
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
    return transform(fs, Functions.<A, B> apply(arg));
  }

  /**
   * Predicate that checks if an iterable is empty.
   * 
   * @return {@code Predicate} which checks if an {@code Iterable} is empty
   * @since 1.1
   */
  public static Predicate<Iterable<?>> isEmpty() {
    return new Predicate<Iterable<?>>() {
      public boolean apply(final Iterable<?> i) {
        return com.google.common.collect.Iterables.isEmpty(i);
      }
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
    return new CollectingIterable<A, B>(from, partial);
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
    return mergeSorted(xss, Ordering.<A> natural());
  }

  /**
   * Filter an {@code Iterable} into a {@code Pair} of {@code Iterable}'s.
   * 
   * @param <A> the type
   * @param iterable to be filtered
   * @param predicate to filter each element
   * @return a pair where the left matches the predicate, and the right does
   * not.
   */
  public static <A> Pair<Iterable<A>, Iterable<A>> partition(Iterable<A> iterable, Predicate<? super A> predicate) {
    return pair(filter(iterable, predicate), filter(iterable, not(predicate)));
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
    checkArgument(n >= 0, "Cannot take a negative number of elements");
    if (xs instanceof List<?>) {
      final List<T> list = (List<T>) xs;
      return list.subList(0, n < list.size() ? n : list.size());
    }
    return new Range<T>(0, n, xs);
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
    checkArgument(n >= 0, "Cannot drop a negative number of elements");
    if (xs instanceof List<?>) {
      final List<T> list = (List<T>) xs;
      if (n > (list.size() - 1)) {
        return ImmutableList.of();
      }
      return ((List<T>) xs).subList(n, list.size());
    }
    return new Range<T>(n, Integer.MAX_VALUE, xs);
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
  public static <A> Iterable<A> mergeSorted(final Iterable<? extends Iterable<A>> xss, final Ordering<A> ordering) {
    return new MergeSortedIterable<A>(xss, ordering);
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
    return new Memoizer<A>(xs);
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
  public static <A, B, C> Function2<Iterable<A>, Iterable<B>, Iterable<C>> zipWith(final Function2<A, B, C> f) {
    return new Function2<Iterable<A>, Iterable<B>, Iterable<C>>() {
      public Iterable<C> apply(final Iterable<A> as, final Iterable<B> bs) {
        return new Zipper<A, B, C>(as, bs, f);
      }
    };
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
    return pair(transform(pairs, Pair.<A> leftValue()), transform(pairs, Pair.<B> rightValue()));
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
    checkArgument(step != 0, "Step must not be zero");
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
    checkArgument(step != 0, "Step must not be zero");
    if (step > 0) {
      checkArgument(start <= end, "Start %s must not be greater than end %s with step %s", start, end, step);
    } else {
      checkArgument(start >= end, "Start %s must not be less than end %s with step %s", start, end, step);
    }

    return new Iterable<Integer>() {
      @Override public Iterator<Integer> iterator() {
        return new UnmodifiableIterator<Integer>() {
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
    };
  }

  //
  // inner classes
  //

  static abstract class IterableToString<A> implements Iterable<A> {
    @Override public final String toString() {
      return com.google.common.collect.Iterables.toString(this);
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
      this.delegate = checkNotNull(delegate);
      this.drop = drop;
      this.size = size;
    }

    public Iterator<A> iterator() {
      return new Iter<A>(drop, size, delegate.iterator());
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
   * Merges two sorted Iterables into one, sorted iterable.
   */
  static final class MergeSortedIterable<A> extends IterableToString<A> {
    private final Iterable<? extends Iterable<A>> xss;
    private final Ordering<A> ordering;

    MergeSortedIterable(final Iterable<? extends Iterable<A>> xss, final Ordering<A> ordering) {
      this.xss = checkNotNull(xss, "xss");
      this.ordering = checkNotNull(ordering, "ordering");
    }

    public Iterator<A> iterator() {
      return new Iter<A>(xss, ordering);
    }

    private static final class Iter<A> extends AbstractIterator<A> {
      private final TreeSet<PeekingIterator<A>> xss;

      private Iter(final Iterable<? extends Iterable<A>> xss, final Ordering<A> ordering) {
        this.xss = newTreeSet(peekingIteratorOrdering(ordering));
        com.google.common.collect.Iterables.addAll(this.xss, transform(filter(xss, not(isEmpty())), peekingIterator()));
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

      private Function<? super Iterable<A>, ? extends PeekingIterator<A>> peekingIterator() {
        return new Function<Iterable<A>, PeekingIterator<A>>() {
          public PeekingIterator<A> apply(final Iterable<A> i) {
            return Iterators.peekingIterator(i.iterator());
          }
        };
      }

      private Ordering<? super PeekingIterator<A>> peekingIteratorOrdering(final Ordering<A> ordering) {
        return new Ordering<PeekingIterator<A>>() {
          public int compare(final PeekingIterator<A> lhs, final PeekingIterator<A> rhs) {
            if (lhs == rhs) {
              return 0;
            }
            return ordering.compare(lhs.peek(), rhs.peek());
          }
        };
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
      this.delegate = checkNotNull(delegate);
      this.partial = checkNotNull(partial);
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
   * Memoizing iterable, maintains a lazily computed linked list of nodes.
   */
  static final class Memoizer<A> extends IterableToString<A> {
    private final Node<A> head;

    Memoizer(final Iterable<A> delegate) {
      head = nextNode(delegate.iterator());
    }

    public Iterator<A> iterator() {
      return new Iter<A>(head);
    }

    private static <A> Node<A> nextNode(final Iterator<A> delegate) {
      return delegate.hasNext() ? new Lazy<A>(delegate) : new End<A>();
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
       * @throws NoSuchElementException if this is terminal
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

      public Node<A> next() throws NoSuchElementException {
        return get();
      }

      public boolean isEnd() {
        return false;
      }

      public A value() {
        return value;
      }
    }

    static class End<A> implements Node<A> {
      public boolean isEnd() {
        return true;
      }

      // /CLOVER:OFF
      public Node<A> next() {
        throw new NoSuchElementException();
      }

      public A value() {
        throw new NoSuchElementException();
      }
      // /CLOVER:ON
    }

    static class Iter<A> extends AbstractIterator<A> {
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
   * Iterable that combines two iterables using a combiner function.
   */
  static class Zipper<A, B, C> extends IterableToString<C> {
    private final Iterable<A> as;
    private final Iterable<B> bs;
    private final Function2<A, B, C> f;

    Zipper(final Iterable<A> as, final Iterable<B> bs, final Function2<A, B, C> f) {
      this.as = checkNotNull(as, "as must not be null.");
      this.bs = checkNotNull(bs, "bs must not be null.");
      this.f = checkNotNull(f, "f must not be null.");
    }

    @Override public Iterator<C> iterator() {
      return new Iter();
    }

    class Iter implements Iterator<C> {
      private final Iterator<A> a = checkNotNull(as.iterator(), "as iterator must not be null.");
      private final Iterator<B> b = checkNotNull(bs.iterator(), "bs iterator must not be null.");

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
  public static <A> Iterable<A> intersperse(Iterable<? extends A> as, A a) {
    return intersperse(as, ofInstance(a));
  }

  /**
   * Intersperse an element between all the elements in an iterable.
   * 
   * @param <A> the type of the elements.
   * @param as the source iterable.
   * @param a the supplier of elements to intersperse between the source elements.
   * @return a new Iterable that intersperses the element between the source.
   * @since 2.3
   */
  public static <A> Iterable<A> intersperse(Iterable<? extends A> as, Supplier<A> a) {
    return new Intersperse<A>(as, a);
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
    return com.google.common.collect.Iterables.size(as);
  }
}

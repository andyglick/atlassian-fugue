package com.atlassian.fugue;

import com.atlassian.util.concurrent.LazyReference;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;

import static com.atlassian.fugue.Iterables.first;
import static com.google.common.base.Predicates.not;
import static java.util.Objects.requireNonNull;

//TODO name this something different?
public class Iterables2 {

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
    return new MergeSortedIterable<>(xss, ordering);
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
   * Merges two sorted Iterables into one, sorted iterable.
   */
  static final class MergeSortedIterable<A> extends IterableToString<A> {
    private final Iterable<? extends Iterable<A>> xss;
    private final Ordering<A> ordering;

    MergeSortedIterable(final Iterable<? extends Iterable<A>> xss, final Ordering<A> ordering) {
      this.xss = requireNonNull(xss, "xss");
      this.ordering = requireNonNull(ordering, "ordering");
    }

    public Iterator<A> iterator() {
      return new Iter<>(xss, ordering);
    }

    private static final class Iter<A> extends AbstractIterator<A> {
      private final TreeSet<PeekingIterator<A>> xss;

      private Iter(final Iterable<? extends Iterable<A>> xss, final Ordering<A> ordering) {
        Ordering<? super PeekingIterator<A>> comparator = peekingIteratorOrdering(ordering);
        Objects.requireNonNull(comparator);
        this.xss = new TreeSet<>(comparator);
        com.google.common.collect.Iterables.addAll(this.xss,
            com.google.common.collect.Iterables.transform(
                com.google.common.collect.Iterables.filter(xss, not(com.google.common.collect.Iterables::isEmpty)), peekingIterator()::apply));
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

    public Iterator<A> iterator() {
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

    static class Iter<A> extends com.atlassian.fugue.AbstractIterator<A> {
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
}

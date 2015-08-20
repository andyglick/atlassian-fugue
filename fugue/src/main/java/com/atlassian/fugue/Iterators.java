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

package com.atlassian.fugue;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for constructing iterables
 *
 * @since 3.0
 */
class Iterators {

  /**
   * Adds all the elements of the iterator to the collectionToModify
   * @param collectionToModify collection to add element to, must not be null
   * @param iterator source of elements to add, must not be null
   * @param <A> element type
   * @return true if any of the elements from iterator were not also in collectionToModify
   *
   * @since 3.0
   */
  static <A> boolean addAll(final Collection<A> collectionToModify, final Iterator<? extends A> iterator) {
    requireNonNull(collectionToModify);
    requireNonNull(iterator);
    boolean wasModified = false;
    while (iterator.hasNext()) {
      wasModified |= collectionToModify.add(iterator.next());
    }
    return wasModified;
  }

  /**
   * Wrap an iterator to add support for the peek operation
   * @param iterator iterator that may not support peek, must not be null
   * @param <A> element type
   * @return iterator that can return the next element without removing it from the iterator
   *
   * @since 3.0
   */
  static <A> Iterators.Peeking<A> peekingIterator(final Iterator<? extends A> iterator) {
    if (iterator instanceof PeekingImpl) {
      // Safe to cast <? extends T> to <T> because PeekingImpl only uses T
      // covariantly (and cannot be subclassed to add non-covariant uses).
      @SuppressWarnings("unchecked")
      final PeekingImpl<A> peeking = (PeekingImpl<A>) iterator;
      return peeking;
    }
    return new PeekingImpl<>(iterator);
  }

  /**
   * Implementation of Iterators.Peeking that avoids peeking unless necessary.
   */
  private static class PeekingImpl<A> implements Iterators.Peeking<A> {

    private final java.util.Iterator<? extends A> iterator;
    private boolean hasPeeked;
    private A peekedElement;

    public PeekingImpl(final Iterator<? extends A> iterator) {
      this.iterator = requireNonNull(iterator);
    }

    @Override public boolean hasNext() {
      return hasPeeked || iterator.hasNext();
    }

    @Override public A next() {
      if (!hasPeeked) {
        return iterator.next();
      }
      final A result = peekedElement;
      hasPeeked = false;
      peekedElement = null;
      return result;
    }

    @Override public void remove() {
      if (hasPeeked) {
        throw new IllegalStateException("Cannot remove an element after peeking");
      }
      iterator.remove();
    }

    @Override public A peek() {
      if (!hasPeeked) {
        peekedElement = iterator.next();
        hasPeeked = true;
      }
      return peekedElement;
    }
  }

  /**
   * Iterator that returns a single element
   * @param a element to return
   * @param <A> element type
   * @return iterator returning only a
   *
   * @since 3.0
   */
  static <A> Iterator<A> singletonIterator(final A a) {
    return new Iterator<A>() {
      boolean done = false;

      @Override public boolean hasNext() {
        return !done;
      }

      @Override public A next() {
        if (done) {
          throw new UnsupportedOperationException("Attempted to call next on empty iterator");
        } else {
          done = true;
          return a;
        }
      }

      @Override public void remove() {
        throw new UnsupportedOperationException("Cannot call remove on this iterator");
      }
    };
  }

  /**
   * Iterator with no values inside
   * @param <A> element type
   * @return empty iterator
   *
   * @since 3.0
   */
  @SuppressWarnings("unchecked") public static <A> Iterator<A> emptyIterator() {
    return (Iterator<A>) EmptyIterator.INSTANCE;
  }

  private enum EmptyIterator implements Iterator<Object> {
    INSTANCE;

    @Override public boolean hasNext() {
      return false;
    }

    @Override public Object next() {
      throw new NoSuchElementException("Attempted to call next on empty iterator");
    }

    @Override public void remove() {
      throw new UnsupportedOperationException("Cannot call remove on this iterator");
    }
  }


  //
  // Implementation classes
  //

  /**
   * Marker interface for use in constructing iterators
   *
   * @since 3.0
   */
  interface Peek<A> {
    /**
     * Look at but do not modify the "next" thing.
     */
    A peek();
  }

  /**
   * Iterator that can examine next without removing it
   *
   * @since 3.0
   * @param <A> element type
   */
  interface Peeking<A> extends Peek<A>, Iterator<A> {}

  /**
   * A template implementation of the {@code Iterator} interface, so clients can
   * more easily implement Iterator for some patterns of iteration.
   *q
   * <P>
   * An example is an iterator that skips over null elements in a backing
   * iterator. This could be implemented as:
   *
   * <pre>
   * {@code
   *
   *   public static Iterator<String> filterNulls(final Iterator<String> in) {
   *     return new AbstractIterator<String>() {
   *       protected String computeNext() {
   *         while (in.hasNext()) {
   *           String s = in.next();
   *           if (s != null) {
   *             return s;
   *           }
   *         }
   *         return endOfData();
   *       }
   *     };
   *   }}
   * </pre>
   *
   * <P>
   * This class supports iterators that include null elements.
   *
   * <P>
   * This class is a re-implentation of the Guava AbstractIterator class.
   * @since 3.0
   */
  static abstract class Abstract<A> extends Unmodifiable<A> {
    private State state = State.NotReady;

    /** Constructor for use by subclasses. */
    protected Abstract() {}

    private enum State {
      Ready, NotReady, Complete, Failed
    }

    private A next;

    /**
     * The next element.
     * <P>
     * <b>Note:</b> the implementation must call {@link #endOfData()} when there
     * are no elements left in the iteration. Failure to do so could result in an
     * infinite loop.
     */
    protected abstract A computeNext();

    /**
     * Implementations of {@link #computeNext} <b>must</b> invoke this method when
     * there are no elements left in the iteration.
     *
     * @return {@code null}; a convenience so your {@code computeNext}
     * implementation can use the simple statement {@code return endOfData();}
     */
    protected final A endOfData() {
      state = State.Complete;
      return null;
    }

    @Override public final boolean hasNext() {
      switch (state) {
        case Failed:
          throw new IllegalStateException("Failed iterator");
        case Ready:
          return true;
        case Complete:
          return false;
        default:
          return tryToComputeNext();
      }
    }

    private boolean tryToComputeNext() {
      try {
        next = computeNext();
        if (state != State.Complete) {
          state = State.Ready;
          return true;
        }
        return false;
      } catch (RuntimeException | Error e) {
        state = State.Failed;
        throw e;
      }
    }

    @Override public final A next() {
      if (!hasNext())
        throw new NoSuchElementException();
      try {
        return next;
      } finally {
        next = null;
        state = State.NotReady;
      }
    }
  }

  /**
   * Iterator where {@link #remove} is unsupported.
   */
  static abstract class Unmodifiable<E> implements Iterator<E> {
    protected Unmodifiable() {}

    @Deprecated @Override public final void remove() {
      throw new UnsupportedOperationException();
    }
  }

}

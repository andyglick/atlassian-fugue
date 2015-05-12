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
   * @param collectionToModify collection to add eleement to
   * @param iterator source of elements to add
   * @param <A> element type
   * @return true if any of the elements from iterator were not also in collectionToModify
   *
   * @since 3.0
   */
  static <A> boolean addAll(Collection<A> collectionToModify, Iterator<? extends A> iterator) {
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
   * @param iterator iterator that may not support peek
   * @param <A> element type
   * @return iterator that can return the next element without removing it from the iterator
   *
   * @since 3.0
   */
  static <A> PeekingIterator<A> peekingIterator(java.util.Iterator<? extends A> iterator) {
    if (iterator instanceof PeekingImpl) {
      // Safe to cast <? extends T> to <T> because PeekingImpl only uses T
      // covariantly (and cannot be subclassed to add non-covariant uses).
      @SuppressWarnings("unchecked")
      PeekingImpl<A> peeking = (PeekingImpl<A>) iterator;
      return peeking;
    }
    return new PeekingImpl<>(iterator);
  }

  /**
   * Implementation of PeekingIterator that avoids peeking unless necessary.
   */
  private static class PeekingImpl<A> implements PeekingIterator<A> {

    private final java.util.Iterator<? extends A> iterator;
    private boolean hasPeeked;
    private A peekedElement;

    public PeekingImpl(java.util.Iterator<? extends A> iterator) {
      this.iterator = requireNonNull(iterator);
    }

    @Override public boolean hasNext() {
      return hasPeeked || iterator.hasNext();
    }

    @Override public A next() {
      if (!hasPeeked) {
        return iterator.next();
      }
      A result = peekedElement;
      hasPeeked = false;
      peekedElement = null;
      return result;
    }

    @Override public void remove() {
      if (hasPeeked) {
        throw new IllegalStateException("Can't remove after you've peeked at next");
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
}

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

import static java.util.Objects.requireNonNull;

/**
 * Utility class for constructing iterables
 *
 * @since 4.0
 */
class Iterators {

  static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
    requireNonNull(addTo);
    requireNonNull(iterator);
    boolean wasModified = false;
    while (iterator.hasNext()) {
      wasModified |= addTo.add(iterator.next());
    }
    return wasModified;
  }

  static <T> PeekingIterator<T> peekingIterator(java.util.Iterator<? extends T> iterator) {
    if (iterator instanceof PeekingImpl) {
      // Safe to cast <? extends T> to <T> because PeekingImpl only uses T
      // covariantly (and cannot be subclassed to add non-covariant uses).
      @SuppressWarnings("unchecked")
      PeekingImpl<T> peeking = (PeekingImpl<T>) iterator;
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
}

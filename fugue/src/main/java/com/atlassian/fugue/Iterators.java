package com.atlassian.fugue;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Function;

import static com.atlassian.fugue.Iterables.filter;
import static com.atlassian.fugue.Iterables.first;
import static com.atlassian.fugue.Iterables.transform;
import static java.util.Objects.requireNonNull;

/**
 * Created by anund on 4/6/15.
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


  static <T> PeekingIterator<T> peekingIterator(
      java.util.Iterator<? extends T> iterator) {
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
  private static class PeekingImpl<E> implements PeekingIterator<E> {

    private final java.util.Iterator<? extends E> iterator;
    private boolean hasPeeked;
    private E peekedElement;

    public PeekingImpl(java.util.Iterator<? extends E> iterator) {
      this.iterator = requireNonNull(iterator);
    }

    @Override
    public boolean hasNext() {
      return hasPeeked || iterator.hasNext();
    }

    @Override
    public E next() {
      if (!hasPeeked) {
        return iterator.next();
      }
      E result = peekedElement;
      hasPeeked = false;
      peekedElement = null;
      return result;
    }

    @Override
    public void remove() {
      if(hasPeeked){
        throw new IllegalStateException("Can't remove after you've peeked at next");
      }
      iterator.remove();
    }

    @Override
    public E peek() {
      if (!hasPeeked) {
        peekedElement = iterator.next();
        hasPeeked = true;
      }
      return peekedElement;
    }
  }
}

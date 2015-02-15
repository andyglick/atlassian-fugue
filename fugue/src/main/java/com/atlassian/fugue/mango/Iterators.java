package com.atlassian.fugue.mango;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by anund on 2/14/15.
 */
public class Iterators {
  private Iterators() {}

  public static <A> Iterator<A> singletonIterator(final A a) {
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

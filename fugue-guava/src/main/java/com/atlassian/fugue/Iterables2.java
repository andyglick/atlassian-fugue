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

import com.atlassian.util.concurrent.LazyReference;

import java.util.Iterator;
import java.util.NoSuchElementException;

//TODO name this something different?
public class Iterables2 {

  //
  // inner classes
  //

  static abstract class IterableToString<A> implements Iterable<A> {
    @Override public final String toString() {
      return com.google.common.collect.Iterables.toString(this);
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

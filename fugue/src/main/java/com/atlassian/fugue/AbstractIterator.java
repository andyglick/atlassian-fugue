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

import java.util.NoSuchElementException;

/**
 * A template implementation of the {@code Iterator} interface, so clients can
 * more easily implement Iterator for some patterns of iteration.
 *
 * <p>
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
 * <p>
 * This class supports iterators that include null elements.
 * 
 * <p>
 * This class is a re-implentation of the Guava AbstractIterator class.
 */
abstract class AbstractIterator<A> extends UnmodifiableIterator<A> {
  private State state = State.NotReady;

  /** Constructor for use by subclasses. */
  protected AbstractIterator() {}

  private enum State {
    Ready, NotReady, Complete, Failed
  }

  private A next;

  /**
   * The next element.
   * <p>
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

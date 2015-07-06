/**
 * Copyright 2008 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.fugue;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.function.Supplier;

import net.jcip.annotations.ThreadSafe;

/**
 * Class supports the implementation of {@link Iterables#memoize(Iterable)} and
 * is not intended for general use.
 *
 * Lazily loaded reference that is not constructed until required. This class is
 * used to maintain a reference to an object that is expensive to create and
 * must be constructed once and once only. This reference behaves as though the
 * <code>final</code> keyword has been used (you cannot reset it once it has
 * been constructed). Object creation is guaranteed to be thread-safe and the
 * first thread that calls {@link #get()} will be the one that creates it.
 * <p>
 * Usage: clients need to implement the {@link #create()} method to return the
 * object this reference will hold.
 * <p>
 * For instance:
 * <p>
 *
 * <pre>
 * final LazyReference&lt;MyObject&gt; ref = new LazyReference() {
 *   protected MyObject create() throws Exception {
 *     // Do expensive object construction here
 *     return new MyObject();
 *   }
 * };
 * </pre>
 *
 * Then call {@link #get()} to get a reference to the referenced object:
 *
 * <pre>
 * MyObject myLazyLoadedObject = ref.get()
 * </pre>
 *
 * NOTE: Interruption policy is that if you want to be cancellable while waiting
 * for another thread to create the value, instead of calling {@link #get()}
 * call {@link #getInterruptibly()}. However, If your {@link #create()} method
 * is interrupted and throws an {@link InterruptedException}, it is treated as
 * an application exception and will be the causal exception inside the runtime
 * {@link InitializationException} that {@link #get()} or
 * {@link #getInterruptibly()} throws and your {@link #create()} will not be
 * called again.
 * <p>
 * This class is NOT {@link Serializable}.
 * <p>
 * Implementation note. This class extends {@link WeakReference} as
 * {@link Reference} does not have a public constructor. WeakReference is
 * preferable as it does not have any members and therefore doesn't increase the
 * memory footprint. As we never pass a referent through to the super-class and
 * override {@link #get()}, the garbage collection semantics of WeakReference
 * are irrelevant. The referenced object will not become eligible for GC unless
 * the object holding the reference to this object is collectible.
 *
 * @param <T> the type of the contained element.
 */
@ThreadSafe abstract class LazyReference<T> extends WeakReference<T> implements Supplier<T> {

  private final Sync sync = new Sync();

  public LazyReference() {
    super(null);
  }

  /**
   * The object factory method, guaranteed to be called once and only once.
   *
   * @return the object that {@link #get()} and {@link #getInterruptibly()} will
   * return.
   * @throws Exception if anything goes wrong, rethrown as an
   * InitializationException from {@link #get()} and {@link #getInterruptibly()}
   */
  protected abstract T create() throws Exception;

  /**
   * Get the lazily loaded reference in a non-cancellable manner. If your
   * <code>create()</code> method throws an Exception calls to
   * <code>get()</code> will throw an InitializationException which wraps the
   * previously thrown exception.
   *
   * @return the object that {@link #create()} created.
   * @throws InitializationException if the {@link #create()} method throws an
   * exception. The {@link InitializationException#getCause()} will contain the
   * exception thrown by the {@link #create()} method
   */
  @Override public final T get() {
    boolean interrupted = false;
    try {
      while (true) {
        try {
          return getInterruptibly();
        } catch (final InterruptedException ignore) {
          // ignore and try again
          interrupted = true;
        }
      }
    } finally {
      if (interrupted) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Get the lazily loaded reference in a cancellable manner. If your
   * <code>create()</code> method throws an Exception, calls to
   * <code>get()</code> will throw a RuntimeException which wraps the previously
   * thrown exception.
   *
   * @return the object that {@link #create()} created.
   * @throws InitializationException if the {@link #create()} method throws an
   * exception. The {@link InitializationException#getCause()} will contain the
   * exception thrown by the {@link #create()} method
   * @throws InterruptedException If the calling thread is Interrupted while
   * waiting for another thread to create the value (if the creating thread is
   * interrupted while blocking on something, the {@link InterruptedException}
   * will be thrown as the causal exception of the
   * {@link InitializationException} to everybody calling this method).
   */
  public final T getInterruptibly() throws InterruptedException {
    if (!sync.isDone()) {
      sync.run();
    }

    try {
      return sync.get();
    } catch (final ExecutionException e) {
      throw new InitializationException(e);
    }
  }

  /**
   * Has the {@link #create()} reference been initialized.
   *
   * @return true if the task is complete
   */
  public final boolean isInitialized() {
    return sync.isDone();
  }

  /**
   * Cancel the initializing operation if it has not already run. Will try and
   * interrupt if it is currently running.
   */
  public final void cancel() {
    sync.cancel(true);
  }

  /**
   * If the factory {@link LazyReference#create()} method threw an exception,
   * this wraps it.
   */
  public static class InitializationException extends RuntimeException {
    private static final long serialVersionUID = 3638376010285456759L;

    InitializationException(final ExecutionException e) {
      super((e.getCause() != null) ? e.getCause() : e);
    }
  }

  static final class State {
    static final int INIT = 0;
    static final int RUNNING = 1;
    static final int RAN = 2;
    static final int CANCELLED = 4;
  }

  /**
   * Synchronization control for LazyReference. Note that this must be a
   * non-static inner class in order to invoke the protected <tt>create</tt>
   * method. Taken from FutureTask AQS implementation and pruned to be as
   * compact as possible.
   *
   * Uses AQS sync state to represent run status.
   */
  private final class Sync extends AbstractQueuedSynchronizer {

    static final int IGNORED = 0;

    /**
     * only here to shut up the compiler warnings, the outer class is NOT
     * serializable
     */
    private static final long serialVersionUID = -1645412544240373524L;

    /** The result to return from get() */
    private T result;
    /** The exception to throw from get() */
    private Throwable exception;

    /**
     * The thread running task. When nulled after set/cancel, this indicates
     * that the results are accessible. Must be volatile, to ensure visibility
     * upon completion.
     */
    private volatile Thread runner;

    private boolean ranOrCancelled(final int state) {
      return (state & (State.RAN | State.CANCELLED)) != State.INIT;
    }

    /**
     * Implements AQS base acquire to succeed if ran or cancelled
     */
    @Override protected int tryAcquireShared(final int ignore) {
      return isDone() ? 1 : -1;
    }

    /**
     * Implements AQS base release to always signal after setting final done
     * status by nulling runner thread.
     */
    @Override protected boolean tryReleaseShared(final int ignore) {
      runner = null;
      return true;
    }

    boolean isDone() {
      return ranOrCancelled(getState()) && (runner == null);
    }

    T get() throws InterruptedException, ExecutionException {
      acquireSharedInterruptibly(IGNORED);
      if (getState() == State.CANCELLED) {
        throw new CancellationException();
      }
      if (exception != null) {
        throw new ExecutionException(exception);
      }
      return result;
    }

    void set(final T v) {
      for (;;) {
        final int s = getState();
        if (s == State.RAN) {
          return;
        }
        if (s == State.CANCELLED) {
          // aggressively release to set runner to null,
          // in case we are racing with a cancel request
          // that will try to interrupt runner
          releaseShared(IGNORED);
          return;
        }
        if (compareAndSetState(s, State.RAN)) {
          result = v;
          releaseShared(IGNORED);
          return;
        }
      }
    }

    void setException(final Throwable t) {
      for (;;) {
        final int s = getState();
        if (s == State.RAN) {
          return;
        }
        if (s == State.CANCELLED) {
          // aggressively release to set runner to null,
          // in case we are racing with a cancel request
          // that will try to interrupt runner
          releaseShared(0);
          return;
        }
        if (compareAndSetState(s, State.RAN)) {
          exception = t;
          result = null;
          releaseShared(0);
          return;
        }
      }
    }

    void cancel(final boolean mayInterruptIfRunning) {
      for (;;) {
        final int s = getState();
        if (ranOrCancelled(s)) {
          return;
        }
        if (compareAndSetState(s, State.CANCELLED)) {
          break;
        }
      }
      if (mayInterruptIfRunning) {
        final Thread r = runner;
        if (r != null) {
          r.interrupt();
        }
      }
      releaseShared(IGNORED);
    }

    void run() {
      if ((getState() != State.INIT) || !compareAndSetState(State.INIT, State.RUNNING)) {
        if (runner == Thread.currentThread()) {
          throw new IllegalMonitorStateException("Not reentrant!");
        }
        return;
      }
      try {
        runner = Thread.currentThread();
        set(create());
      } catch (final Throwable ex) {
        setException(ex);
      }
    }
  }
}

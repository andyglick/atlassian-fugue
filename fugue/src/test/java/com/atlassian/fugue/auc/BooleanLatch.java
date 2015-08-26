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

package com.atlassian.fugue.auc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

/**
 * Class exists to support testing LazyReference it is not intended for general
 * use. See atlassian.util.concurrent.BooleanLatch
 *
 * A {@link BooleanLatch} is a reusable latch that resets after it is released
 * and waited on. It depends on a boolean condition of being released or not and
 * becomes unreleased when one thread successfully awaits it. It is useful for
 * rally like release-wait-release coordination, and as a replacement to waiting
 * on a {@link Condition} (it should be faster as the write thread does not need
 * to acquire a lock in order to signal.
 * <p>
 * This latch is suitable for SRSW coordination. MRSW is supported but has the
 * same semantics as {@link Condition#signal()}, that is to say that
 * {@link Condition#signalAll()} is not supported and if there are multiple
 * waiters then the particular thread that is released is arbitrary.
 */
// @ThreadSafe
public class BooleanLatch implements ReusableLatch {
  /**
   * Synchronization control For BooleanLatch. Uses AQS state to represent
   * released state.
   */
  private static class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = -3475411235403448115L;

    private static final int RELEASED = 0;
    private static final int UNAVAILABLE = -1;

    private Sync() {
      setState(UNAVAILABLE);
    }

    @Override protected boolean tryAcquire(final int ignore) {
      if (!(getState() == RELEASED)) {
        return false;
      }
      return compareAndSetState(RELEASED, UNAVAILABLE);
    }

    @Override protected boolean tryRelease(final int ignore) {
      final int state = getState();
      if (state == UNAVAILABLE) {
        setState(RELEASED);
      }
      return true;
    }
  }

  private final Sync sync = new Sync();

  /**
   * {@inheritDoc}
   *
   * Releases at most one waiting thread. If the current state is released then
   * nothing happens.
   */
  public final void release() {
    sync.release(0);
  }

  /**
   * {@inheritDoc}
   *
   * Causes the current thread to wait until the latch has been released, unless
   * the thread is {@linkplain Thread#interrupt() interrupted}.
   * <p>
   * If the latch has already been released then this method returns
   * immediately.
   * <p>
   * If the latch is not released then the current thread becomes disabled for
   * thread scheduling purposes and lies dormant until one of two things happen:
   * <ul>
   * <li>The latch is released by another thread invoking the {@link #release()}
   * method; or
   * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current
   * thread.
   * </ul>
   * <p>
   * If the current thread:
   * <ul>
   * <li>has its interrupted status set on entry to this method; or
   * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
   * </ul>
   * then {@link InterruptedException} is thrown and the current thread's
   * interrupted status is cleared.
   *
   * @throws InterruptedException if the current thread is interrupted while
   * waiting
   */
  public final void await() throws InterruptedException {
    sync.acquireInterruptibly(0);
  }

  /**
   * {@inheritDoc}
   *
   * Causes the current thread to wait until the latch has been released, unless
   * the thread is {@linkplain Thread#interrupt() interrupted}, or the specified
   * waiting time elapses.
   * <p>
   * If the latch has already been released then this method returns immediately
   * with return value true.
   * <p>
   * If the latch is unreleased then the current thread becomes disabled for
   * thread scheduling purposes and lies dormant until one of three things
   * happen:
   * <ul>
   * <li>The latch is released by another thread invoking the {@link #release()}
   * method; or
   * <li>Some other thread {@linkplain Thread#interrupt interrupts} the current
   * thread; or
   * <li>The specified waiting time elapses.
   * </ul>
   * <p>
   * If latch is released by another thread then the method returns with the
   * value {@code true}.
   * <p>
   * If the current thread:
   * <ul>
   * <li>has its interrupted status set on entry to this method; or
   * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
   * </ul>
   * then {@link InterruptedException} is thrown and the current thread's
   * interrupted status is cleared.
   * <p>
   * If the specified waiting time elapses then the value {@code false} is
   * returned. If the time is less than or equal to zero, the method will not
   * wait at all.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the {@code timeout} argument
   * @return {@code true} if the count reached zero and {@code false} if the
   * waiting time elapsed before the count reached zero
   * @throws InterruptedException if the current thread is interrupted while
   * waiting
   */
  public final boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
    return sync.tryAcquireNanos(0, unit.toNanos(timeout));
  }
}
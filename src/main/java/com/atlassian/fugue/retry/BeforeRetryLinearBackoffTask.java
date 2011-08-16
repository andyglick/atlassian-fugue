package com.atlassian.fugue.retry;

import com.google.common.base.Preconditions;

/**
 * A backoff task for use in a retry -function, -supplier, or -task. This should
 * be used as the beforeRetry hook. Upon each execution, the current thread
 * sleeps for the time specified at construction.
 * 
 * This class is threadsafe and contains no internal state, hence instances can
 * be reused and the same instance can be used on multiple threads.
 */
public class BeforeRetryLinearBackoffTask implements Runnable {
  private final long backoff;

  /**
   * @param backoffMillis the time to wait whenever run is executed
   */
  public BeforeRetryLinearBackoffTask(long backoffMillis) {
    Preconditions.checkArgument(backoffMillis > 0, "Backoff time must not be negative.");
    this.backoff = backoffMillis;
  }

  /**
   * This method causes the current thread to sleep for the time specified when
   * the instance is constructed. InterruptedExceptions are wrapped before being
   * rethrown in a RuntimeException.
   */
  public void run() {
    try {
      Thread.sleep(backoff);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  long currentBackoff() {
    return backoff;
  }
}

package com.atlassian.fugue.retry;

import com.google.common.base.Preconditions;

/**
 * A backoff task for use in a retry -function, -supplier, or -task. This should
 * be used as the beforeRetry hook. Upon each execution, the amount of time to
 * wait before retrying the function call is doubled.
 * 
 * This class maintains an internal state; we recommend creating a new instance
 * for each use.
 */
public class BeforeRetryExponentialBackoffTask implements Runnable {
  private long backoff;

  /**
   * @param backoffMillis the amount of time to wait, in milliseconds before
   * retrying the first time. This is doubled for each subsequent retry. This
   * parameter must be above zero.
   */
  public BeforeRetryExponentialBackoffTask(long backoffMillis) {
    Preconditions.checkArgument(backoffMillis > 0, "Backoff time must not be negative.");
    this.backoff = backoffMillis;
  }

  /**
   * This method causes the current thread to sleep for a duration which doubles
   * after each successive call. InterruptedExceptions are wrapped before being
   * rethrown in a RuntimeException.
   */
  public void run() {
    try {
      Thread.sleep(backoff);
      backoff = backoff * 2;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

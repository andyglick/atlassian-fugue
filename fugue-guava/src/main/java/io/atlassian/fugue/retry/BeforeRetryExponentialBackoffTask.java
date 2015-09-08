/*
   Copyright 2010 Atlassian

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
package io.atlassian.fugue.retry;

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
    if (backoffMillis <= 0) {
      throw new IllegalArgumentException("Backoff time must not be negative.");
    }
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

  long currentBackoff() {
    return backoff;
  }
}

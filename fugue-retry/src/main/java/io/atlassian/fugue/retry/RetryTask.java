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

import static java.util.Objects.requireNonNull;

/**
 * A Runnable which wraps the apply method of another Runnable and attempts it a
 * fixed number of times. This class can be used when a task is known to be
 * prone to occasional failure and other workarounds are not known.
 *
 * @see RetrySupplier for a Supplier implementation
 * @see RetryFunction for a Function implementation
 * @see RetryFactory for some factory methods
 * @see ExceptionHandlers for some predefined handlers
 */
public class RetryTask implements Runnable {
  private RetrySupplier<?> retrySupplier;

  /**
   * An instance that does nothing before retrying and ignores exceptions that
   * occur.
   *
   * @param task to run, must not be null
   * @param tries number of times to attempt to run task, must be posititve
   */
  public RetryTask(final Runnable task, int tries) {
    this(task, tries, ExceptionHandlers.ignoreExceptionHandler());
  }

  /**
   * An instance that does nothing before retrying.
   *
   * @param task to run, must not be null
   * @param tries number of times to attempt to run task, must be positive
   * @param handler reacts to exceptions thrown by the wrapped task, must not be
   * null
   */
  public RetryTask(final Runnable task, int tries, ExceptionHandler handler) {
    this(task, tries, handler, new NoOp());
  }

  /**
   * <p>
   * Constructor for RetryTask.
   * </p>
   *
   * @param task to run, must not be null
   * @param tries number of times to attempt to run task, must be positive
   * @param handler reacts to exceptions thrown by the wrapped task, must not be
   * null
   * @param beforeRetry runs before each retry, must not be null
   */
  public RetryTask(final Runnable task, int tries, ExceptionHandler handler, Runnable beforeRetry) {
    requireNonNull(task, "task");

    retrySupplier = new RetrySupplier<>(() -> {
      task.run();
      return null;
    }, tries, handler, beforeRetry);
  }

  /**
   * {@inheritDoc}
   *
   * Attempt to run the wrapped Runnable <i>tries</i> number of times. Any
   * exceptions thrown will be ignored until the number of attempts is reached.
   * If the number of attempts is reached without success, the most recent
   * exception to be thrown will be rethrown.
   */
  @Override public void run() {
    retrySupplier.get();
  }
}

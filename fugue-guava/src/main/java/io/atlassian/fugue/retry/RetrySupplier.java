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

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A Supplier which wraps the apply method of another Supplier and attempts it
 * up to a fixed number of times. This class can be used when a task is known to
 * be prone to occasional failure and other workarounds are not known.
 *
 * @param <T> The type of the result the Supplier yields upon application
 * @see RetryFunction for a Function implementation
 * @see RetryFactory for some factory methods
 * @see ExceptionHandlers for some predefined handlers
 */
public class RetrySupplier<T> implements Supplier<T> {
  private final Supplier<T> supplier;
  private final int tries;
  private final ExceptionHandler handler;
  private final Runnable beforeRetry;

  /**
   * An instance that does nothing before retrying and ignores exceptions that
   * occur.
   *
   * @param supplier which fetches the result, must not be null
   * @param tries the number of times to attempt to get a result, must be
   * positive
   */
  public RetrySupplier(Supplier<T> supplier, int tries) {
    this(supplier, tries, ExceptionHandlers.ignoreExceptionHandler());
  }

  /**
   * An instance that does nothing before retrying.
   *
   * @param supplier which fetches the result, must not be null
   * @param tries the number of times to attempt to get a result, must be
   * positive
   * @param handler reacts to exceptions thrown by the supplier, must not be
   * null
   */
  public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler) {
    this(supplier, tries, handler, new NoOp());
  }

  /**
   * <p>
   * Constructor for RetrySupplier.
   * </p>
   *
   * @param supplier which fetches the result, must not be null
   * @param tries the number of times to attempt to get a result, must be
   * positive
   * @param handler reacts to exceptions thrown by the supplier, must not be
   * null
   * @param beforeRetry a task which will run at the end of any
   */
  public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler, Runnable beforeRetry) {
    requireNonNull(supplier);
    if (tries <= 0) {
      throw new IllegalArgumentException("Tries must be strictly positive");
    }
    requireNonNull(handler);

    this.beforeRetry = beforeRetry;
    this.supplier = supplier;
    this.tries = tries;
    this.handler = handler;
  }

  /**
   * {@inheritDoc}
   *
   * Attempt to get a result <i>tries</i> number of times. Any exceptions thrown
   * will be ignored until the number of attempts is reached. If the number of
   * attempts is reached without a successful result, the most recent exception
   * to be thrown will be rethrown.
   */
  @Override public T get() {
    RuntimeException ex = null;
    for (int i = 0; i < tries; i++) {
      try {
        return supplier.get();
      } catch (RuntimeException e) {
        handler.handle(e);
        ex = e;
      }

      if (i + 1 < tries) {
        beforeRetry.run();
      }
    }
    throw ex;
  }
}

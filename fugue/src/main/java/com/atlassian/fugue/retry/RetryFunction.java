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
package com.atlassian.fugue.retry;

import static com.atlassian.fugue.mango.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import com.atlassian.fugue.mango.Preconditions;

/**
 * A Function which wraps the apply method of another Function and attempts it
 * up to a fixed number of times. This class can be used when a task is known to
 * be prone to occasional failure and other workarounds are not known.
 * 
 * @param <F> The type of the parameter the Function accepts
 * @param <T> The type of the result the Function yields upon application
 * @see RetrySupplier for a Supplier implementation
 * @see RetryTask for a Runnable implementation
 * @see RetryFactory for some factory methods
 */
public class RetryFunction<F, T> implements Function<F, T> {
  private final Function<F, T> function;
  private final int tries;
  private final ExceptionHandler handler;
  private final Runnable beforeRetry;

  /**
   * An instance that does nothing before retrying and ignores exceptions that
   * occur.
   * 
   * @param function which fetches the result, must not be null
   * @param tries the numbe rof times to attempt to get a result, must be
   * positive
   */
  public RetryFunction(Function<F, T> function, int tries) {
    this(function, tries, ExceptionHandlers.ignoreExceptionHandler());
  }

  /**
   * An instance that does nothing before retrying.
   * 
   * @param function which fetches the result, must not be null
   * @param tries the number of times to attempt to get a result, must be
   * positive
   * @param handler reacts to exceptions thrown by the supplier, must not be
   * null
   */
  public RetryFunction(Function<F, T> function, int tries, ExceptionHandler handler) {
    this(function, tries, handler, new NoOp());
  }

  /**
   * @param function which fetches the result, must not be null
   * @param tries the number of times to attempt to get a result, must be
   * positive
   * @param handler reacts to exceptions thrown by the supplier, must not be
   * null
   * @param beforeRetry an effect that is run before a retry attempt
   */
  public RetryFunction(Function<F, T> function, int tries, ExceptionHandler handler, Runnable beforeRetry) {

    this.function = checkNotNull(function);
    this.handler = checkNotNull(handler);
    Preconditions.checkArgument(tries >= 0, "Tries must not be negative");
    this.tries = tries;
    this.beforeRetry = checkNotNull(beforeRetry);
  }

  /**
   * Attempt to apply <i>parameter</i> to the wrapped Function <i>tries</i>
   * number of times. Any exceptions thrown will be ignored until the number of
   * attempts is reached. If the number of attempts is reached without a
   * successful result, the most recent exception to be thrown will be rethrown.
   * 
   * @return the result of the wrapped Function's get method
   */
  @Override public T apply(F parameter) {
    return new RetrySupplier<T>(Suppliers.compose(function, Suppliers.ofInstance(parameter)), tries, handler, beforeRetry).get();
  }
}

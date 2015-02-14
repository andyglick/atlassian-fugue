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

import com.atlassian.fugue.mango.Function.Function;
import com.atlassian.fugue.mango.Function.Supplier;

/**
 * Provides factory methods for RetryFunction, RetryTask, and RetrySupplier.
 * These classes can be used when a task is known to fail on occasion and no
 * other workaround is known.
 * 
 * This class is not instantiable.
 */
public class RetryFactory {
  private RetryFactory() {
    throw new AssertionError("This class is non-instantiable.");
  }

  /**
   * Decorates a runnable so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param task which will be wrapped for retrial. It should be idempotent on
   * failure.
   * @param tries the number of times to re-attempt the call
   * @return a runnable which can be used to call another runnable multiple
   * times when that runnable may fail sporadically
   */
  public static Runnable create(Runnable task, int tries) {
    return create(task, tries, ExceptionHandlers.ignoreExceptionHandler());
  }

  /**
   * Decorates a runnable so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param task which will be wrapped for retrial. It should be idempotent on
   * failure.
   * @param tries the number of times to re-attempt the call
   * @param handler passed any exceptions that are encountered
   * @return a runnable which can be used to call another runnable multiple
   * times when that runnable may fail sporadically
   */
  public static Runnable create(Runnable task, int tries, ExceptionHandler handler) {
    return new RetryTask(task, tries, handler);
  }

  /**
   * Decorates a runnable so that it retries a number of times before being
   * allowed to fail, backing off exponentially in time.
   * 
   * @param task which will be wrapped for retrial. It should be idempotent on
   * failure.
   * @param tries the number of times to re-attempt the call
   * @param handler which acts on exceptions thrown by the wrapped supplier
   * @param backoff time to wait in millis each time
   * @return a runnable which can be used to call another runnable multiple
   * times when that runnable may fail sporadically
   */
  public static Runnable create(Runnable task, int tries, ExceptionHandler handler, long backoff) {
    return new RetryTask(task, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
  }

  /**
   * Decorates a supplier so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param supplier which will be wrapped for retrial. It should be idempotent
   * on failure.
   * @param tries the number of times to re-attempt the call
   * @param <A> The type of the object returned by supplier
   * @return a supplier which can be used to call another supplier multiple
   * times when that supplier may fail sporadically
   */
  public static <A> Supplier<A> create(Supplier<A> supplier, int tries) {
    return create(supplier, tries, ExceptionHandlers.ignoreExceptionHandler());
  }

  /**
   * Decorates a supplier so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param supplier which will be wrapped for retrial. It should be idempotent
   * on failure.
   * @param tries the number of times to re-attempt the call
   * @param <A> The type of the object returned by supplier
   * @param handler which acts on exceptions thrown by the wrapped supplier
   * @return a supplier which can be used to call another supplier multiple
   * times when that supplier may fail sporadically
   */
  public static <A> Supplier<A> create(Supplier<A> supplier, int tries, ExceptionHandler handler) {
    return new RetrySupplier<A>(supplier, tries, handler);
  }

  /**
   * Decorates a supplier so that it retries a number of times before being
   * allowed to fail, backing-off in time exponentially.
   * 
   * @param <A> The type of the object returned by supplier
   * @param supplier which will be wrapped for retrial. It should be idempotent
   * on failure.
   * @param tries the number of times to re-attempt the call
   * @param handler which acts on exceptions thrown by the wrapped supplier
   * @param backoff time to wait in millis each time
   * @return a supplier which can be used to call another supplier multiple
   * times when that supplier may fail sporadically
   */
  public static <A> Supplier<A> create(Supplier<A> supplier, int tries, ExceptionHandler handler, long backoff) {
    return new RetrySupplier<A>(supplier, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
  }

  /**
   * Decorates a function so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param <A> the type of the parameter the function accepts
   * @param <B> the type of the result of the function's apply method
   * @param function which will be wrapped for retrial. It should be idempotent
   * on failure.
   * @param tries the number of times to re-attempt the call
   * @return a function which can be used to invoke another function multiple
   * times when that function may fail sporadically
   */
  public static <A, B> Function<A, B> create(Function<A, B> function, int tries) {
    return create(function, tries, ExceptionHandlers.ignoreExceptionHandler());
  }

  /**
   * Decorates a function so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param <A> the type of the parameter the function accepts
   * @param <B> the type of the result of the function's apply method
   * @param function which will be wrapped for retrial. It should be idempotent
   * on failure.
   * @param tries the number of times to re-attempt the call
   * @param handler which acts on exceptions thrown by the wrapped supplier
   * @return a function which can be used to invoke another function multiple
   * times when that function may fail sporadically
   */
  public static <A, B> Function<A, B> create(Function<A, B> function, int tries, ExceptionHandler handler) {
    return create(function, tries, handler, 0);
  }

  /**
   * Decorates a function so that it retries a number of times before being
   * allowed to fail.
   * 
   * @param function which will be wrapped for retrial. It should be idempotent
   * on failure.
   * @param tries the number of times to re-attempt the call
   * @param <A> the type of the parameter the function accepts
   * @param <B> the type of the result of the function's apply method
   * @param handler which acts on exceptions thrown by the wrapped supplier
   * @param backoff time to wait in millis each time
   * @return a function which can be used to invoke another function multiple
   * times when that function may fail sporadically
   */
  public static <A, B> Function<A, B> create(Function<A, B> function, int tries, ExceptionHandler handler, long backoff) {
    return new RetryFunction<A, B>(function, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
  }
}

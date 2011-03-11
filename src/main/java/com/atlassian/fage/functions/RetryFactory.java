package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Provides factory methods for RetryFunction and RetrySupplier.
 * 
 * This class is not instantiable.
 */
public class RetryFactory
{
    private RetryFactory(){throw new AssertionError("This class is non-instantiable.");}

    
    /**
     * Decorates a runnable so that it retries a number of times before being allowed to fail.
     * 
     * @param task which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @return a runnable which can be used to call another runnable multiple times when that runnable may fail
     * sporadically 
     */
    public static Runnable create(Runnable task, int tries, ExceptionHandler handler)
    {
        return new RetryTask(task, tries, handler);
    }
    
    /**
     * Decorates a runnable so that it retries a number of times before being allowed to fail.
     * 
     * @param task which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param handler which acts on exceptions thrown by the wrapped supplier
     * @return a runnable which can be used to call another runnable multiple times when that runnable may fail
     * sporadically 
     */
    public static Runnable create(Runnable task, int tries, ExceptionHandler handler, long backoff)
    {
        return new RetryTask(task, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
    }
    
    /**
     * Decorates a supplier so that it retries a number of times before being allowed to fail.
     * 
     * @param supplier which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param <T> The type of the object returned by supplier
     * @return a supplier which can be used to call another supplier multiple times when that supplier may fail
     * sporadically 
     */
    public static <T> Supplier<T> create(Supplier<T> supplier, int tries)
    {
        return create(supplier, tries, ExceptionHandlers.ignoreExceptionHandler());
    }
    
    /**
     * Decorates a supplier so that it retries a number of times before being allowed to fail.
     * 
     * @param supplier which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param <T> The type of the object returned by supplier
     * @param handler which acts on exceptions thrown by the wrapped supplier
     * @return a supplier which can be used to call another supplier multiple times when that supplier may fail
     * sporadically 
     */
    public static <T> Supplier<T> create(Supplier<T> supplier, int tries, ExceptionHandler handler)
    {
        return new RetrySupplier<T>(supplier, tries, handler);
    }
    
    /**
     * Decorates a supplier so that it retries a number of times before being allowed to fail.
     * 
     * @param supplier which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param <T> The type of the object returned by supplier
     * @param handler which acts on exceptions thrown by the wrapped supplier
     * @return a supplier which can be used to call another supplier multiple times when that supplier may fail
     * sporadically 
     */
    public static <T> Supplier<T> create(Supplier<T> supplier, int tries, ExceptionHandler handler, long backoff)
    {
        return new RetrySupplier<T>(supplier, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
    }

    /**
     * Decorates a function so that it retries a number of times before being allowed to fail.
     * 
     * @param function which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param <F> the type of the parameter the function accepts
     * @param <T> the type of the result of the function's apply method
     * @return a function which can be used to invoke another function multiple times when that function may fail
     * sporadically 
     */
    public static <F, T> Function<F, T> create(Function<F, T> function, int tries)
    {
        return create(function, tries, ExceptionHandlers.ignoreExceptionHandler());
    }
    
    /**
     * Decorates a function so that it retries a number of times before being allowed to fail.
     * 
     * @param function which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param <F> the type of the parameter the function accepts
     * @param <T> the type of the result of the function's apply method
     * @param handler which acts on exceptions thrown by the wrapped supplier
     * @return a function which can be used to invoke another function multiple times when that function may fail
     * sporadically 
     */
    public static <F, T> Function<F, T> create(Function<F, T> function, int tries, ExceptionHandler handler)
    {
        return create(function, tries, handler, 0);
    }
    
    /**
     * Decorates a function so that it retries a number of times before being allowed to fail.
     * 
     * @param function which will be wrapped for retrial. It should be idempotent.
     * @param tries the number of times to re-attempt the call
     * @param <F> the type of the parameter the function accepts
     * @param <T> the type of the result of the function's apply method
     * @param handler which acts on exceptions thrown by the wrapped supplier
     * @return a function which can be used to invoke another function multiple times when that function may fail
     * sporadically 
     */
    public static <F, T> Function<F, T> create(Function<F, T> function, int tries, ExceptionHandler handler, long backoff)
    {
        return new RetryFunction<F, T>(function, tries, handler, new BeforeRetryExponentialBackoffTask(backoff));
    }
        
}

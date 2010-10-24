package com.atlassian.fage.functions;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * A Supplier which wraps the apply method of another Supplier and attempts it a fixed number of times. This class can
 * be used when a task is known to be prone to occasional failure and other workarounds are not known.
 * 
 * @param <T> The type of the result the Supplier yields upon application
 * @see RetryFunction for a Function implementation
 * @see RetryFactory for some factory methods
 * @see ExceptionHandlers for some predefined handlers
 */
public class RetrySupplier<T> implements Supplier<T>
{
    private final Supplier<T> supplier;
    private final int tries;
    private final ExceptionHandler handler;
    private final Runnable beforeRetry;


    /**
     * @param supplier which fetches the result, must not be null
     * @param tries the number of times to attempt to get a result, must be positive
     * @param handler reacts to exceptions thrown by the supplier, must not be null
     */
    public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler)
    {
        this(supplier, tries, handler, new NoOpBeforeRetryTask());
    }
    
    /**
     * @param supplier which fetches the result, must not be null
     * @param tries the number of times to attempt to get a result, must be positive
     * @param handler reacts to exceptions thrown by the supplier, must not be null
     * @param beforeRetry a task which will run at the end of any 
     */
    public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler, Runnable beforeRetry)
    {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument(tries >= 0, "Tries must not be negative");
        Preconditions.checkNotNull(handler);

        this.beforeRetry = beforeRetry;
        this.supplier = supplier;
        this.tries = tries;
        this.handler = handler;
    }

    /**
     * Attempt to get a result <i>tries</i> number of times. Any exceptions thrown will be ignored until the number of
     * attempts is reached. If the number of attempts is reached without a successful result, the most recent exception
     * to be thrown will be rethrown.
     * 
     * @return the result of the wrapped Supplier's get method
     */
    @Override
    public T get()
    {
        RuntimeException ex = null;
        for (int i = 0; i < tries; i++)
        {
            try
            {
                return supplier.get();
            }
            catch (RuntimeException e)
            {
                handler.handle(e);
                ex = e;
            }
            
            if (i + 1 < tries)
            {
                beforeRetry.run();
            }
        }
        throw ex;
    }
}

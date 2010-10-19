package com.atlassian.fage.functions;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * A Supplier which wraps the apply method of another Supplier and attempts it a fixed number of times. This class can
 * be used when a task is known to be prone to occasional failure and other workarounds are not known.
 * 
 * @param <T> The type of the result the Supplier yields upon application
 * @see RetryFunction for a Function implementation
 * @see Attempt for some factory methods
 * @See ExceptionHandlers for some predefined handlers
 */
public class RetrySupplier<T> implements Supplier<T>
{
    private final Supplier<T> supplier;
    private final int tries;
    private final ExceptionHandler handler;

    public RetrySupplier(Supplier<T> supplier, int tries, ExceptionHandler handler)
    {
        Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument(tries >= 0, "Tries must not be negative");
        Preconditions.checkNotNull(handler);
        
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
        }
        throw ex;
    }
}

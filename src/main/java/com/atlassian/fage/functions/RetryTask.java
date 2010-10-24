package com.atlassian.fage.functions;

import com.google.common.base.Supplier;

/**
 * A Runable which wraps the apply method of another Runnable and attempts it a fixed number of times. This class can
 * be used when a task is known to be prone to occasional failure and other workarounds are not known.
 * 
 * @see RetrySupplier for a Supplier implementation
 * @see RetryFunction for a Function implementation
 * @see RetryFactory for some factory methods
 * @see ExceptionHandlers for some predefined handlers
 */
public class RetryTask implements Runnable
{
    private RetrySupplier<?> retrySupplier;

    public RetryTask(final Runnable task, int tries, ExceptionHandler handler, Runnable beforeRetry)
    {
        retrySupplier = new RetrySupplier<Object>(new Supplier<Object>(){
            public Object get() {
                task.run();
                return null;
            }
        }, tries, handler, beforeRetry);
    }
    
    public RetryTask(final Runnable task, int tries, ExceptionHandler handler)
    {
        this(task, tries, handler, new NoOpBeforeRetryTask());
    }

    /**
     * Attempt to run the wrapped Runnable <i>tries</i> number of times. Any exceptions thrown will be ignored until the
     * number of attempts is reached. If the number of attempts is reached without success, the most recent exception to
     * be thrown will be rethrown.
     */
    @Override
    public void run()
    {
        retrySupplier.get();
    }
}

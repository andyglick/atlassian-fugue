package com.atlassian.fugue.functions;

import com.google.common.base.Supplier;
import com.google.common.base.Preconditions;

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

    /**
     * An instance that does nothing before retrying and ignores exceptions that occur.
     *
     * @param task to run, must not be null
     * @param tries number of times to attempt to run task, must be posititve
     */
    public RetryTask(final Runnable task, int tries)
    {
        this(task, tries, ExceptionHandlers.ignoreExceptionHandler());
    }

    /**
     * An instance that does nothing before retrying.
     *
     * @param task to run, must not be null
     * @param tries number of times to attempt to run task, must be positive
     * @param handler reacts to exceptions thrown by the wrapped task, must not be null
     */
    public RetryTask(final Runnable task, int tries, ExceptionHandler handler)
    {
        this(task, tries, handler, new NoOpBeforeRetryTask());
    }

    /**
     * @param task to run, must not be null
     * @param tries number of times to attempt to run task, must be positive
     * @param handler reacts to exceptions thrown by the wrapped task, must not be null
     * @param beforeRetry runs before each retry, must not be null
     */
    public RetryTask(final Runnable task, int tries, ExceptionHandler handler, Runnable beforeRetry)
    {
        Preconditions.checkNotNull(task, "task");

        retrySupplier = new RetrySupplier<Object>(new Supplier<Object>(){
            public Object get() {
                task.run();
                return null;
            }
        }, tries, handler, beforeRetry);
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

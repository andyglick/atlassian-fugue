package com.atlassian.fage.functions;

import com.google.common.base.Preconditions;

/**
 * This class maintains an internal state; we recommend creating a new instance for each use.
 */
public class BeforeRetryExponentialBackoffTask implements Runnable
{
    private long backoff;

    public BeforeRetryExponentialBackoffTask(long backoffMillis)
    {
        Preconditions.checkArgument(backoffMillis > 0, "Backoff time must not be negative.");
        this.backoff = backoffMillis;
    }

    public void run()
    {
        try
        {
            Thread.sleep(backoff);
            backoff = backoff * 2;
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}

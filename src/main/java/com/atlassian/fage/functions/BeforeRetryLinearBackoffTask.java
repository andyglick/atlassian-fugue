package com.atlassian.fage.functions;

import com.google.common.base.Preconditions;

public class BeforeRetryLinearBackoffTask implements Runnable
{
    private final long backoffMillis;

    public BeforeRetryLinearBackoffTask(long backoffMillis)
    {
        Preconditions.checkArgument(backoffMillis > 0, "Backoff time must not be negative.");
        this.backoffMillis = backoffMillis;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(backoffMillis);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}

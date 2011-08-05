package com.atlassian.fugue.retry;

/**
 * Used in place of a backoff task when no special behaviour (such as a delay)
 * is desired before reattempting a task.
 */
class NoOpBeforeRetryTask implements Runnable
{
    public void run() {}
}

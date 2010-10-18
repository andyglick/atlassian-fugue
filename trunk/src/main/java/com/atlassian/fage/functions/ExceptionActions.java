package com.atlassian.fage.functions;

import org.slf4j.Logger;

/**
 * Provides some standard implementations of various exception Actions.
 */
public class ExceptionActions
{
    private ExceptionActions() {throw new IllegalStateException("This class is not instantiable.");}

    /**
     * Retrieves an {ExceptionAction} which will log exceptions passed in.
     * 
     * @param log the Logger to which exceptions will be logged
     * @return an {ExceptionAction} which will log (at WARN level) exceptions passed in
     */
    public static ExceptionAction loggingExceptionAction(final Logger log)
    {
        return new ExceptionAction() 
        {
            public void act(Exception e)
            {
                if(log != null)
                {
                    log.warn("Exception encountered: ", e);
                }
            }
        };
    }

    /**
     * Retrieves an {ExceptionAction} which will log exceptions passes in and also delay for a period whenever it is
     * called. This is useful as a retry tool when the failure is due to timing issues, such as a
     * ConcurrentModificationException being thrown. If the delay is interrupted, an exception is logged and normal
     * execution is resumed; in this case the delay may not be as long as specified.
     * 
     * @param log the Logger to which exceptions will be logged
     * @param delayMilliseconds the desired duration of the delay, in milliseconds
     * @return an {ExceptionAction} which will log (at WARN level) exceptions passed in and then wait before returning
     */
    public static ExceptionAction loggingDelayingExceptionAction(final Logger log, final int delayMilliseconds)
    {
        final ExceptionAction loggingExceptionAction = loggingExceptionAction(log);
    
        return new ExceptionAction()
        {
            public void act(Exception e)
            {
                loggingExceptionAction.act(e);
                try
                {
                    Thread.sleep(delayMilliseconds);
                }
                catch (InterruptedException iE)
                {
                    log.warn("Interrupted: ", iE);
                }
            }
        };
    }


}

package com.atlassian.fage.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides some standard implementations of various exception Actions.
 */
public class ExceptionActions
{
    private static final Logger log = LoggerFactory.getLogger(ExceptionActions.class);
    
    private ExceptionActions() {throw new IllegalStateException("This class is not instantiable.");}

    /**
     * Retrieves an {ExceptionAction} which will log exceptions passed in.
     * 
     * @param logger the Logger to which exceptions will be logged; if it is null, a default Logger will be used
     * @return an {ExceptionAction} which will log (at WARN level) exceptions passed in
     */
    public static ExceptionAction loggingExceptionAction(final Logger logger)
    {
        return new ExceptionAction() 
        {
            @Override
            public void act(final Exception e)
            {
                warn(logger == null ? log: logger, e); 
            }
            
            private void warn(Logger log, Exception e)
            {
                log.warn("Exception encountered: ", e);
            }
        };
    }

    /**
     * Retrieves an {ExceptionAction} which will delay for a period whenever it is called. This is useful as a retry
     * tool when the failure is due to timing issues, such as a ConcurrentModificationException being thrown or an http
     * request which fails intermittently when temporarily under load. If the delay is interrupted, an exception is
     * logged and normal execution is resumed; in this case the delay may not be as long as specified.
     * 
     * @param log the Logger to which exceptions will be logged
     * @param delayMilliseconds the desired duration of the delay, in milliseconds
     * @return an {ExceptionAction} which will sleep before returning
     */
    public static ExceptionAction delayingExceptionAction(final int delayMilliseconds)
    {
        return new ExceptionAction()
        {
            @Override
            public void act(final Exception e)
            {
                try
                {
                    Thread.sleep(delayMilliseconds);
                }
                catch (InterruptedException iE)
                {
                    log.warn("Quietly swallowing thread interruption and continuing.", iE);
                }
            }
        };
    }

    public static ExceptionAction noOpExceptionAction()
    {
        return new ExceptionAction() { public void act (Exception a) {} };
    }
    /**
     * Chain a series of Actions together to be executed subsequently; if one throws an exception, subsequent actions 
     * will not be executed.
     */
    public static ExceptionAction chain(final ExceptionAction... actions)
    {
        return new ExceptionAction()
        {
            @Override
            public void act(final Exception e)
            {
                for (ExceptionAction action : actions)
                {
                    action.act(e);
                }
            }
        };
    }


}

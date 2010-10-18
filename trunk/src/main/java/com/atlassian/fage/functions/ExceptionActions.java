package com.atlassian.fage.functions;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public static ExceptionAction loggingExceptionAction(Logger logger)
    {
        return new LoggingExceptionAction(logger == null ? log : logger);
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
    public static ExceptionAction delayingExceptionAction(int delayMilliseconds)
    {
        return new DelayingExceptionAction(delayMilliseconds);
    }

    /**
     * @return an {ExceptionAction} which does nothing
     */
    public static ExceptionAction noOpExceptionAction()
    {
        return new NoOpExceptionAction();
    }
    
    /**
     * Chain a series of Actions together to be executed subsequently; if one throws an exception, subsequent actions 
     * will not be executed.
     */
    public static ExceptionAction chain(ExceptionAction... actions)
    {
        return new CompositeExceptionAction(actions);
    }

    private static class NoOpExceptionAction implements ExceptionAction
    {
        public void act (Exception a) {/* do nothing */}
    }

    private static class DelayingExceptionAction implements ExceptionAction
    {
        private final int delayMilliseconds;

        public DelayingExceptionAction(int delayMilliseconds)
        {
            Preconditions.checkArgument(delayMilliseconds >= 0, "The delay must not be negative");
            this.delayMilliseconds = delayMilliseconds;
        }

        @Override
        public void act(Exception e)
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
    }

    private static class LoggingExceptionAction implements ExceptionAction
    {
        private final Logger logger;

        public LoggingExceptionAction(Logger logger)
        {
            this.logger = logger;
        }

        @Override
        public void act(Exception e)
        {
            warn(logger, e); 
        }

        private void warn(Logger log, Exception e)
        {
            log.warn("Exception encountered: ", e);
        }
    }

    private static class CompositeExceptionAction implements ExceptionAction
    {
        private final ExceptionAction[] actions;

        public CompositeExceptionAction(ExceptionAction... actions)
        {
            checkNotNull(actions);
            this.actions = actions;
        }

        @Override
        public void act(Exception e)
        {
            for (ExceptionAction action : actions)
            {
                action.act(e);
            }
        }
    }
}

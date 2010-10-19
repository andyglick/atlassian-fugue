package com.atlassian.fage.functions;

import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides some standard implementations of various exception Actions.
 * 
 * This class is not instantiable.
 */
public class ExceptionHandlers
{
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);
    
    private ExceptionHandlers() {throw new AssertionError("This class is not instantiable.");}

    /**
     * Retrieves an {@link ExceptionHandler} which will log exceptions passed in.
     * 
     * @param logger the Logger to which exceptions will be logged; if it is null, a default Logger will be used
     * @return an {@link ExceptionHandler} which will log (at WARN level) exceptions passed in
     */
    public static ExceptionHandler loggingExceptionHandler(Logger logger)
    {
        return new LoggingExceptionHandler(logger == null ? log : logger);
    }

    /**
     * Retrieves an {@link ExceptionHandler} which will delay for a period whenever it is called. This is useful as a retry
     * tool when the failure is due to timing issues, such as a ConcurrentModificationException being thrown or an http
     * request which fails intermittently when temporarily under load. If the delay is interrupted, an exception is
     * logged and normal execution is resumed; in this case the delay may not be as long as specified.
     * 
     * @param delayMilliseconds the desired duration of the delay, in milliseconds
     * @return an {@link ExceptionHandler} which will sleep before returning
     */
    public static ExceptionHandler delayingExceptionHandler(int delayMilliseconds)
    {
        return new DelayingExceptionHandler(delayMilliseconds);
    }

    /**
     * @return an {@link ExceptionHandler} which does nothing
     */
    public static ExceptionHandler noOpExceptionHandler()
    {
        return new NoOpExceptionHandler();
    }
    
    /**
     * Chain a series of ExceptionHandlers together to be executed subsequently; if one throws an exception, subsequent
     * handlers will not be executed.
     */
    public static ExceptionHandler chain(ExceptionHandler... handlers)
    {
        return new CompositeExceptionHandler(handlers);
    }

    private static class NoOpExceptionHandler implements ExceptionHandler
    {
        public void handle(Exception a) {/* do nothing */}
    }

    private static class DelayingExceptionHandler implements ExceptionHandler
    {
        private final int delayMilliseconds;

        public DelayingExceptionHandler(int delayMilliseconds)
        {
            Preconditions.checkArgument(delayMilliseconds >= 0, "The delay must not be negative");
            this.delayMilliseconds = delayMilliseconds;
        }

        @Override
        public void handle(Exception e)
        {
            try
            {
                Thread.sleep(delayMilliseconds);
            }
            catch (InterruptedException iE)
            {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
                
                // I can't foresee a situation in which this should happen under normal use, but it isn't an error
                // per se, so log a warning.
                log.warn("Thread interrupted", iE);
            }
        }
    }

    private static class LoggingExceptionHandler implements ExceptionHandler
    {
        private final Logger logger;

        public LoggingExceptionHandler(Logger logger)
        {
            this.logger = logger;
        }

        @Override
        public void handle(Exception e)
        {
            warn(logger, e); 
        }

        private void warn(Logger log, Exception e)
        {
            log.warn("Exception encountered: ", e);
        }
    }

    private static class CompositeExceptionHandler implements ExceptionHandler
    {
        private final ExceptionHandler[] handlers;

        public CompositeExceptionHandler(ExceptionHandler... handlers)
        {
            checkNotNull(handlers);
            this.handlers = handlers;
        }

        @Override
        public void handle(Exception e)
        {
            for (ExceptionHandler handler : handlers)
            {
                handler.handle(e);
            }
        }
    }
}

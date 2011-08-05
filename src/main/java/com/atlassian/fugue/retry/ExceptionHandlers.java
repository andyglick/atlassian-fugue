package com.atlassian.fugue.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides some standard implementations of various exception actions.
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
     * @param logger the Logger to which exceptions will be logged; if it is null, a default Logger will be used. The default logger
     * is the logger for the ExceptionHandlers class, but may change in future.
     * @return an {@link ExceptionHandler} which will log (at WARN level) exceptions passed in
     */
    public static ExceptionHandler loggingExceptionHandler(Logger logger)
    {
        return new LoggingExceptionHandler(logger == null ? log : logger);
    }

    /**
     * @return an {@link ExceptionHandler} which does nothing
     */
    public static ExceptionHandler ignoreExceptionHandler()
    {
        return new IgnoreExceptionHandler();
    }
    
    /**
     * Chain a series of ExceptionHandlers together to be executed subsequently; if one throws an exception, subsequent
     * handlers will not be executed.
     */
    public static ExceptionHandler chain(ExceptionHandler... handlers)
    {
        return new CompositeExceptionHandler(handlers);
    }

    private static class IgnoreExceptionHandler implements ExceptionHandler
    {
        public void handle(RuntimeException a) {/* do nothing */}
    }

    private static class LoggingExceptionHandler implements ExceptionHandler
    {
        private final Logger logger;

        public LoggingExceptionHandler(Logger logger)
        {
            this.logger = logger;
        }

        @Override
        public void handle(RuntimeException e)
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
        public void handle(RuntimeException e)
        {
            for (ExceptionHandler handler : handlers)
            {
                handler.handle(e);
            }
        }
    }
}

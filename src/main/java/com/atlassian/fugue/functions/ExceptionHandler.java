package com.atlassian.fugue.functions;

/**
 * Takes action when an Exception is thrown. Examples include placing a delay in execution when performing back-offs and
 * logging errors when exceptions are encountered.
 * 
 * @see ExceptionHandlers for some predefined handlers
 */
public interface ExceptionHandler
{
    /**
     * Act on an exception, this method should be called by clients when an exception occurs in wrapped code.
     */
    void handle(RuntimeException exception);
}

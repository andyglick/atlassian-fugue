package com.atlassian.fage.functions;

/**
 * Takes action when an Exception is thrown. Examples include a delay in execution when performing back-offs and
 * logging errors when exceptions are encountered.
 * @see ExceptionActions for some predefined handlers
 */
public interface ExceptionAction
{
    /**
     * Act on an exception, this method should be called by clients when an exception occurs in wrapped code.
     */
    void act(Exception exception);
}

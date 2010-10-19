package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Provides factory methods for RetryFunction and RetrySupplier.
 */
public class Attempt
{
    private Attempt(){throw new AssertionError("This class is non-instantiable.");}

    public static <T> Supplier<T> attempt(Supplier<T> supplier, int tries)
    {
        return attempt(supplier, tries, ExceptionHandlers.noOpExceptionHandler());
    }
    
    public static <T> Supplier<T> attempt(Supplier<T> supplier, int tries, ExceptionHandler handler)
    {
        return new RetrySupplier<T>(supplier, tries, handler);
    }

    public static <F, T> Function<F, T> attempt(Function<F, T> function, int tries)
    {
        return attempt(function, tries, ExceptionHandlers.noOpExceptionHandler());
    }
    
    public static <F, T> Function<F, T> attempt(Function<F, T> function, int tries, ExceptionHandler handler)
    {
        return new RetryFunction<F, T>(function, tries, handler);
    }
}

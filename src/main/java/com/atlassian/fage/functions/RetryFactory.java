package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Provides factory methods for RetryFunction and RetrySupplier.
 * 
 * This class is not instantiable.
 */
public class RetryFactory
{
    private RetryFactory(){throw new AssertionError("This class is non-instantiable.");}

    public static <T> Supplier<T> create(Supplier<T> supplier, int tries)
    {
        return create(supplier, tries, ExceptionHandlers.noOpExceptionHandler());
    }
    
    public static <T> Supplier<T> create(Supplier<T> supplier, int tries, ExceptionHandler handler)
    {
        return new RetrySupplier<T>(supplier, tries, handler);
    }

    public static <F, T> Function<F, T> create(Function<F, T> function, int tries)
    {
        return create(function, tries, ExceptionHandlers.noOpExceptionHandler());
    }
    
    public static <F, T> Function<F, T> create(Function<F, T> function, int tries, ExceptionHandler handler)
    {
        return new RetryFunction<F, T>(function, tries, handler);
    }
}

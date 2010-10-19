package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides methods that can be used to attempt to execute a function or supplier a number of times.
 */
public class Attempt
{
    private Attempt(){throw new AssertionError("This class is non-instantiable.");}

    /**
     * Attempt to get the result of supplier <i>tries</i> number of times. Any exceptions thrown by the supplier will be
     * ignored until the number of attempts is reached. If the number of attempts is reached without a successful result,
     * the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the supplier
     */
    public static <T> T attempt(Supplier<T> supplier, int tries)
    {
        return attempt(supplier, tries, ExceptionHandlers.noOpExceptionHandler());
    }
    
    /**
     * Attempt to get the result of supplier <i>tries</i> number of times. Any exceptions thrown by the supplier will be
     * acted upon by the exception handler provided until the number of attempts is reached. If the number of attempts is
     * reached without a successful result, the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the supplier
     */
    public static <T> T attempt(Supplier<T> supplier, int tries, ExceptionHandler handler)
    {
        checkNotNull(supplier);
        checkNotNull(handler);
        
        RuntimeException ex = null;
        for (int i = 0; i < tries; i++)
        {
            try
            {
                return supplier.get();
            }
            catch (RuntimeException e)
            {
                handler.handle(e);
                ex = e;
            }
        }
        throw ex;
    }
    
    /**
     * Attempt to get the result of function <i>tries</i> number of times. Any exceptions thrown by the function will be
     * ignored until the number of attempts is reached. If the number of attempts is reached without a successful result,
     * the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the function
     */
    public static <F, T> T attempt(Function<F, T> function, F parameter, int tries)
    {
        return attempt(function, parameter, tries, ExceptionHandlers.noOpExceptionHandler());
    }
    
    /**
     * Attempt to get the result of function <i>tries</i> number of times. Any exceptions thrown by the function will be
     * acted upon by the exception handler provided until the number of attempts is reached. If the number of attempts is
     * reached without a successful result, the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the supplier
     */
    public static <F, T> T attempt(Function<F, T> function, F parameter, int tries, ExceptionHandler handler)
    {
        checkNotNull(function);
        checkNotNull(handler);
        
        return attempt(toSupplier(function, parameter), tries, handler);
    }

    private static <F, T> Supplier<T> toSupplier(final Function<F, T> function, final F parameter)
    {
        return Suppliers.compose(function, Suppliers.ofInstance(parameter));
    }
}

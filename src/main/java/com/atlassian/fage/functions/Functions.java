package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A place to put useful functions.
 */
public class Functions
{
    private Functions(){throw new AssertionError("This class is non-instantiable.");}

    /**
     * Attempt to get the result of supplier <i>tries</i> number of times. Any exceptions thrown by the supplier will be
     * ignored until the number of attempts is reached. If the number of attempts is reached without a successful result,
     * the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the supplier
     */
    public static <T> T attempt(Supplier<T> supplier, int tries)
    {
        return attempt(supplier, tries, ExceptionActions.noOpExceptionAction());
    }
    
    /**
     * Attempt to get the result of supplier <i>tries</i> number of times. Any exceptions thrown by the supplier will be
     * acted upon by the exception action provided until the number of attempts is reached. If the number of attempts is
     * reached without a successful result, the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the supplier
     */
    public static <T> T attempt(Supplier<T> supplier, int tries, ExceptionAction action)
    {
        checkNotNull(supplier);
        checkNotNull(action);
        
        RuntimeException ex = null;
        for (int i = 0; i < tries; i++)
        {
            try
            {
                return supplier.get();
            }
            catch (RuntimeException e)
            {
                action.act(e);
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
        return attempt(function, parameter, tries, ExceptionActions.noOpExceptionAction());
    }
    
    /**
     * Attempt to get the result of function <i>tries</i> number of times. Any exceptions thrown by the function will be
     * acted upon by the exception action provided until the number of attempts is reached. If the number of attempts is
     * reached without a successful result, the most recent exception to be thrown will be thrown again.
     * 
     * @return the first successful result from the supplier
     */
    public static <F, T> T attempt(Function<F, T> function, F parameter, int tries, ExceptionAction action)
    {
        checkNotNull(function);
        checkNotNull(action);
        
        return attempt(toSupplier(function, parameter), tries, action);
    }

    private static <F, T> Supplier<T> toSupplier(final Function<F, T> function, final F parameter)
    {
        return Suppliers.compose(function, Suppliers.ofInstance(parameter));
    }
}

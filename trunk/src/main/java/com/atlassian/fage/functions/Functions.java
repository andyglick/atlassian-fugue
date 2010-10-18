package com.atlassian.fage.functions;
import com.google.common.base.Supplier;

/**
 * A place to put useful functions.
 */
public class Functions
{
    private Functions(){throw new IllegalStateException("This class is non-instantiable");}

    /**
     * Attempt to get the result of supplier <i>tries</i> number of times. Any exceptions thrown by the supplier will be
     * ignored until the number of attempts is reached. If the number of attempts is reached without a successful result,
     * a RuntimeException is thrown wrapping the most recent exception thrown by the supplier.
     * 
     * @return the first successful result from the supplier
     */
    public static <T> T attempt(Supplier<T> supplier, int tries)
    {
        return attempt(supplier, tries, null);
    }
    
    /**
     * Attempt to get the result of supplier <i>tries</i> number of times. Any exceptions thrown by the supplier will be
     * handled by the exception handler provided until the number of attempts is reached. If the number of attempts is
     * reached without a successful result, a RuntimeException will be thrown wrapping the most recent exception thrown
     * by the supplier.
     * 
     * @return the first successful result from the supplier
     */
    public static <T> T attempt(Supplier<T> supplier, int tries, ExceptionAction handler)
    {
        Exception ex = null;
        for (int i = 0; i < tries; i++)
        {
            try
            {
                return supplier.get();
            }
            catch (Exception e)
            {
                if (handler != null)
                {
                    handler.act(e);
                }
                ex = e;
            }
        }
        throw new RuntimeException(ex);
    }

}

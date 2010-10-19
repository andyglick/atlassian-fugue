package com.atlassian.fage.functions;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;

/**
 * A Function which wraps the apply method of another Function and attempts it a fixed number of times. This class can
 * be used when a task is known to be prone to occasional failure and other workarounds are not known.
 * 
 * @param <F> The type of the parameter the Function accepts
 * @param <T> The type of the result the Function yields upon application
 * @see RetrySupplier for a Supplier implementation
 * @see Attempt for some factory methods
 */
public class RetryFunction<F, T> implements Function<F, T>
{
    private final Function<F, T> function;
    private final int tries;
    private final ExceptionHandler handler;

    public RetryFunction(Function<F, T> function, int tries, ExceptionHandler handler)
    {
        Preconditions.checkNotNull(function);
        Preconditions.checkArgument(tries >= 0, "Tries must not be negative");
        Preconditions.checkNotNull(handler);
        
        this.function = function;
        this.tries = tries;
        this.handler = handler;
    }

    /**
     * Attempt to apply <i>parameter</i> to the wrapped Function <i>tries</i> number of times. Any exceptions thrown 
     * will be ignored until the number of attempts is reached. If the number of attempts is reached without a
     * successful result, the most recent exception to be thrown will be rethrown.
     * 
     * @return the result of the wrapped Function's get method
     */
    @Override
    public T apply(F parameter)
    {
        return new RetrySupplier<T>(Suppliers.compose(function, Suppliers.ofInstance(parameter)), tries, handler).get();
    }
}

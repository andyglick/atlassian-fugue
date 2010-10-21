package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * Represents a left fold function with the initial value and function to be applied already known. 
 * 
 * @param <A> the type of the elements to be processed
 * @param <R> the type of the result
 */
@Beta
public class LeftFolder<A, R> implements Function<Iterable<A>, R>
{
    private final R initialValue;
    private final Function2Arg<R, A, R> f;

    /**
     * @param initialValue the initial value
     * @param f the function to be applied
     */
    public LeftFolder(R initialValue, Function2Arg<R, A, R> f)
    {
        this.initialValue = initialValue;
        this.f = f;
    }

    /**
     * Applies the function to the initial value and all elements of as to generate an R
     */
    @Override
    public R apply(final Iterable<A> as)
    {
        R result = initialValue;
        for (A a : as)
        {
            result = f.apply(result, a);
        }
        return result;
    }
}

package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

@Beta
/**
 * Utility class containing various fold methods. In general a fold applies a certain function to each element in a list
 * and aggregates the result. A left fold applies the function in the natural traversal order whilst the right fold
 * applies them in the reverse order.
 */
public class Fold
{
    private Fold() { throw new AssertionError("This class is uninstantiable."); }
    
    public static <A, R> Function2Arg<R, A, R> uncurry(final Function<A, R> convertor, final Function<R, Function<R, R>> generator)
    {
        return new UncurriedFunction<R, A, R>(convertor, generator);
    }
    
    public static <A, R> R right(final Function<A, R> convertor, final Function<R, Function<R, R>> combinerGenerator, R initialValue, Iterable<A> vs)
    {
        return right(uncurry(convertor, combinerGenerator), initialValue, vs);
    }
    
    public static <A, R> R right(Function2Arg<R, A, R> f, R initialValue, Iterable<A> vs)
    {
        return withRight(f, initialValue).apply(vs);
    }
    
    public static <A, R> Function<Iterable<A>, R> withRight(Function2Arg<R, A, R> f, R initialValue)
    {
        return new RightFolder<A, R>(initialValue, f);
    }

    public static <A, R> R left(final Function<A, R> convertor, final Function<R, Function<R, R>> combinerGenerator, R initialValue, Iterable<A> vs)
    {
        return left(uncurry(convertor, combinerGenerator), initialValue, vs);
    }
    
    public static <A, R> R left(Function2Arg<R, A, R> f, R initialValue, Iterable<A> ss)
    {
        return with(f, initialValue).apply(ss);
    }

    public static <A, R> Function<Iterable<A>, R> with(Function2Arg<R, A, R> f, R initialValue)
    {
        return new LeftFolder<A, R>(initialValue, f);
    }

}


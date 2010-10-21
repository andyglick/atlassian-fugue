package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * Utility class containing various fold methods. In general a fold applies a certain function to each element in a list
 * and aggregates the result. A left fold applies the function in the natural traversal order whilst the right fold
 * applies it in the reverse order.
 */
@Beta
public class Fold
{
    private Fold() { throw new AssertionError("This class is uninstantiable."); }

    /**
     * This method is double beta. I'd use the @Alpha annotation, but there isn't one and this isn't important enough to
     * write one.
     */
    @Beta
    public static <A, R> Function2Arg<R, A, R> uncurry(Function<A, R> convertor, Function<R, Function<R, R>> generator)
    {
        return new UncurriedFunction<R, A, R>(convertor, generator);
    }
    
    /**
     * This method is double beta. I'd use the @Alpha annotation, but there isn't one and this isn't important enough to
     * write one.
     */
    @Beta
    public static <A, R> R right(Function<A, R> convertor, Function<R, Function<R, R>> combinerGenerator, R initialValue, Iterable<A> vs)
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

    /**
     * This method is double beta. I'd use the @Alpha annotation, but there isn't one and this isn't important enough to
     * write one.
     */
    @Beta
    public static <A, R> R left(Function<A, R> convertor, Function<R, Function<R, R>> combinerGenerator, R initialValue, Iterable<A> vs)
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


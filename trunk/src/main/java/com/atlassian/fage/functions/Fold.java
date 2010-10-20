package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

@Beta
/**
 * Utility class containing various fold methods.
 */
public class Fold
{
    private Fold() { throw new AssertionError("This class is uninstantiable."); }
    
    public static <U, V> Function2Arg<U, U, V> uncurry(final Function<V, U> convertor, final Function<U, Function<U, U>> generator)
    {
        return new UncurriedFunction<U, V>(convertor, generator);
    }
    
    public static <U, V> U right(final Function<V, U> convertor, final Function<U, Function<U, U>> combinerGenerator, U initialValue, Iterable<V> vs)
    {
        return right(uncurry(convertor, combinerGenerator), initialValue, vs);
    }
    
    public static <U, V> U right(Function2Arg<U, U, V> f, U initialValue, Iterable<V> vs)
    {
        return withRight(f, initialValue).apply(vs);
    }
    
    public static <U, V> Function<Iterable<V>, U> withRight(Function2Arg<U, U, V> f, U initialValue)
    {
        return new RightFolder<V, U>(initialValue, f);
    }

    public static <U, V> U left(final Function<V, U> convertor, final Function<U, Function<U, U>> combinerGenerator, U initialValue, Iterable<V> vs)
    {
        return left(uncurry(convertor, combinerGenerator), initialValue, vs);
    }
    
    public static <U, V> U left(Function2Arg<U, U, V> f, U initialValue, Iterable<V> ss)
    {
        return with(f, initialValue).apply(ss);
    }

    public static <U, V> Function<Iterable<V>, U> with(Function2Arg<U, U, V> f, U initialValue)
    {
        return new LeftFolder<V, U>(initialValue, f);
    }

}


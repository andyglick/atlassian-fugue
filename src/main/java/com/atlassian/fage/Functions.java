package com.atlassian.fage;

import com.google.common.base.Function;

public class Functions
{
    public static <IterableType, ReturnType> ReturnType fold (Function2<ReturnType, IterableType, ReturnType> f, ReturnType initialValue,  Iterable<IterableType> elements)
    {
        ReturnType currentValue = initialValue;
        for (IterableType element: elements)
        {
            currentValue = f.apply(currentValue, element);
        }
        return currentValue;
    }
    
    public static <IterableType, ReturnType> ReturnType fold (Function<Tuple<ReturnType, IterableType>, ReturnType> f, ReturnType initialValue,  Iterable<IterableType> elements)
    {
        return fold(curry(f), initialValue, elements);
    }

    private static <IterableType, ReturnType> Function2<ReturnType, IterableType, ReturnType> curry(final Function<Tuple<ReturnType, IterableType>, ReturnType> f)
    {
        return new Function2<ReturnType, IterableType, ReturnType>()
        {
            @Override
            public ReturnType apply(ReturnType arg1, IterableType arg2)
            {
                return f.apply(new Tuple<ReturnType, IterableType>(arg1, arg2));
            }
        };
    }

    /*
    public static <IterableType, ReturnType> Function2<ReturnType, Iterable<IterableType>, ReturnType> fold(final Function2<ReturnType, IterableType, ReturnType> f)
    {
        return new Function2<ReturnType, Iterable<IterableType>, ReturnType>()
        {
            @Override
            public ReturnType apply(ReturnType arg1, Iterable<IterableType> arg2)
            {
                return fold(f, arg1, arg2);
            }
        };
    }
    
    public static <IterableType, ReturnType> Function<Iterable<IterableType>, ReturnType> fold (final Function2<ReturnType, IterableType, ReturnType> f, final ReturnType initialValue)
    {
        return new Function<Iterable<IterableType>, ReturnType> ()
        {
            @Override
            public ReturnType apply(Iterable<IterableType> iterableTypeIterable)
            {
                return fold(f, initialValue, iterableTypeIterable);
            }
        };
    }*/
}

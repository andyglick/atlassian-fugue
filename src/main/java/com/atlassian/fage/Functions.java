package com.atlassian.fage;

import com.google.common.base.Function;

public class Functions
{
    public static <F, T> T fold (Function2<T, F, T> f, T initialValue,  Iterable<F> elements)
    {
        T currentValue = initialValue;
        for (F element: elements)
        {
            currentValue = f.apply(currentValue, element);
        }
        return currentValue;
    }
    
    public static <F, T> T fold (Function<Tuple<T, F>, T> f, T initialValue,  Iterable<F> elements)
    {
        return fold(curry(f), initialValue, elements);
    }

    private static <F, T> Function2<T, F, T> curry(final Function<Tuple<T, F>, T> f)
    {
        return new Function2<T, F, T>()
        {
            @Override
            public T apply(T arg1, F arg2)
            {
                return f.apply(new Tuple<T, F>(arg1, arg2));
            }
        };
    }
}

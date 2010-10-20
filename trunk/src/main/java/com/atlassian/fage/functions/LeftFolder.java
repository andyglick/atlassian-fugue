package com.atlassian.fage.functions;

import com.google.common.base.Function;

class LeftFolder<V, U> implements Function<Iterable<V>, U>
{
    private final U initialValue;
    private final Function2Arg<U, U, V> f;

    public LeftFolder(U initialValue, Function2Arg<U, U, V> f)
    {
        this.initialValue = initialValue;
        this.f = f;
    }

    @Override
    public U apply(final Iterable<V> vs)
    {
        U result = initialValue;
        for (V u : vs)
        {
            result = f.apply(result, u);
        }
        return result;
    }
}

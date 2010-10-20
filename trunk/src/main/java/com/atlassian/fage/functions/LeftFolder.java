package com.atlassian.fage.functions;

import com.google.common.base.Function;

class LeftFolder<A, R> implements Function<Iterable<A>, R>
{
    private final R initialValue;
    private final Function2Arg<R, A, R> f;

    public LeftFolder(R initialValue, Function2Arg<R, A, R> f)
    {
        this.initialValue = initialValue;
        this.f = f;
    }

    @Override
    public R apply(final Iterable<A> vs)
    {
        R result = initialValue;
        for (A u : vs)
        {
            result = f.apply(result, u);
        }
        return result;
    }
}

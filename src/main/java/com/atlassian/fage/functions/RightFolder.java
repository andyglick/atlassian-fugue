package com.atlassian.fage.functions;

import java.util.Deque;
import java.util.LinkedList;

import com.google.common.base.Function;

class RightFolder<A, R> implements Function<Iterable<A>, R>
{
    private final LeftFolder<A, R> folder;

    public RightFolder(R initialValue, Function2Arg<R, A, R> f)
    {
        this.folder = new LeftFolder<A, R>(initialValue, f);
    }

    @Override
    public R apply(final Iterable<A> vs)
    {
        return folder.apply(reverse(vs));
    }

    private static <V> Iterable<V> reverse(final Iterable<V> vs)
    {
        // sure, Queues don't support nulls, but LinkedLists do and we don't care/
        Deque<V> q = new LinkedList<V>();
        for(V v : vs)
        {
            q.addFirst(v);
        }
        return q;
    }
    
}

package com.atlassian.fage.functions;

import java.util.Deque;
import java.util.LinkedList;

import com.google.common.base.Function;

class RightFolder<V, U> implements Function<Iterable<V>, U>
{
    private final LeftFolder<V, U> folder;

    public RightFolder(U initialValue, Function2Arg<U, U, V> f)
    {
        this.folder = new LeftFolder<V, U>(initialValue, f);
    }

    @Override
    public U apply(final Iterable<V> vs)
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

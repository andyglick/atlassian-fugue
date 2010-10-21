package com.atlassian.fage.functions;

import java.util.Deque;
import java.util.LinkedList;

import com.google.common.base.Function;

/**
 * Represents a right fold function with the initial value and function to be applied already known. 
 * 
 * @param <A> the type of the elements to be processed
 * @param <R> the type of the result
 */
public class RightFolder<A, R> implements Function<Iterable<A>, R>
{
    private final LeftFolder<A, R> folder;

    /**
     * @param initialValue the initial value
     * @param f the function to be applied
     */
    public RightFolder(R initialValue, Function2Arg<R, A, R> f)
    {
        this.folder = new LeftFolder<A, R>(initialValue, f);
    }

    /**
     * Applies the function to all elements of as and the initial value to generate an R
     */
    @Override
    public R apply(final Iterable<A> as)
    {
        return folder.apply(reverse(as));
    }

    private static <V> Iterable<V> reverse(final Iterable<V> vs)
    {
        // sure, Queues don't support nulls, but LinkedLists do and we don't care.
        Deque<V> q = new LinkedList<V>();
        for(V v : vs)
        {
            q.addFirst(v);
        }
        return q;
    }
    
}

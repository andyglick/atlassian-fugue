package com.atlassian.fage;

import com.google.common.base.Preconditions;

public class Tuple<A,B>
{
    private final A left;
    private final B right;

    public Tuple(A left, B right)
    {
        Preconditions.checkNotNull(left, "Left parameter must not be null.");
        Preconditions.checkNotNull(right, "Right parameter must not be null.");
        
        this.left = left;
        this.right = right;
    }
    
    public <C> Tuple cons(C c)
    {
        return new Tuple(c, this);
    }
    
    public A left()
    {
        return left;
    }
    
    public B right()
    {
        return right;
    }
}

package com.atlassian.fage;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Pair<A,B>
{
    private static final int HALF_WORD = 16;
    
    public static <A,B> Pair<A,B> pair(A left, B right)
    {
        return new Pair(left, right);
    }

    private final A left;
    private final B right;

    public Pair(A left, B right)
    {
        this.left = checkNotNull(left, "Left parameter must not be null.");
        this.right = checkNotNull(right, "Right parameter must not be null.");
    }
    
    public <C> Pair cons(C c)
    {
        return new Pair(c, this);
    }
    
    public A left()
    {
        return left;
    }
    
    public B right()
    {
        return right;
    }

    @Override
    public String toString()
    {
        return "Pair: (a: {" + left + "}, b: {" + right + "})";
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (this == o)
            return true;

        if (! (o instanceof Pair))
            return false;

        Pair that = (Pair) o;

        return left.equals(that.left) && right.equals(that.right);
    }

    @Override
    public int hashCode() {
        int leftHash = left.hashCode();
        int rghtHash = right.hashCode();

        return (((leftHash >> HALF_WORD) ^ leftHash) << HALF_WORD) |
               (((rghtHash << HALF_WORD) ^ rghtHash) >> HALF_WORD);
    }
}

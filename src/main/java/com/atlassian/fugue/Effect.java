package com.atlassian.fugue;

public interface Effect<A>
{
    void apply(A a);
}

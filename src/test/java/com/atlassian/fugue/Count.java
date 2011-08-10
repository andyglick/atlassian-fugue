package com.atlassian.fugue;

/**
 * Simply counts how many times it was applied.
 */
class Count<A> implements Effect<A>
{
    static <A> int countEach(final Effect.Applicant<A> a)
    {
        final Count<A> counter = new Count<A>();
        a.foreach(counter);
        return counter.count();
    }

    private int count = 0;

    public void apply(final A a)
    {
        count++;
    }

    public int count()
    {
        return count;
    }
}

package com.atlassian.fugue;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class EitherTest
{
    @Test (expected = NullPointerException.class)
    public void testNullLeft()
    {
        Either.left(null);
    }

    @Test
    public void testLeftCreation()
    {
        Either<Boolean, Integer> left = Either.left(Boolean.TRUE);
        assertTrue(left.isLeft());
        assertEquals(Boolean.TRUE, left.left().get());
    }

    @Test (expected = NullPointerException.class)
    public void testNullRight()
    {
        Either.right(null);
    }

    @Test
    public void testRightCreation()
    {
        Either<Boolean, Integer> right = Either.right(1);
        assertTrue(right.isRight());
        assertEquals(new Integer(1), right.right().get());
    }

    @Test
    public void testLeftMerge()
    {
        Either<String, String> left = Either.left("Ponies.");
        String actual = Either.merge(left);
        assertEquals("Ponies.", actual);
    }

    @Test
    public void testRightMerge()
    {
        Either<String, String> right = Either.right("Unicorns.");
        String actual = Either.merge(right);
        assertEquals("Unicorns.", actual);
    }

    @Test
    public void testCondTrue()
    {
        Either<Integer, String> cond = Either.cond(true, "Pegasus.", 7);
        assertEquals(Either.right("Pegasus."), cond);
    }

    @Test
    public void testCondFalse()
    {
        Either<Integer, String> cond = Either.cond(false, "Pegasus.", 7);
        assertEquals(Either.left(7), cond);
    }
}

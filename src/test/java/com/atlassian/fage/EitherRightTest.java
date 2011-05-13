package com.atlassian.fage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class EitherRightTest
{
    private static final Integer ORIGINAL_VALUE = 1;
    final Either<Boolean, Integer> either = Either.right(ORIGINAL_VALUE);

    @Test
    public void testRight()
    {
        assertEquals(ORIGINAL_VALUE, either.right().get());
    }

    @Test
    public void testLeft()
    {
        assertFalse(either.left().isDefined());
    }

    @Test
    public void testIsRight()
    {
        assertTrue(either.isRight());
    }

    @Test
    public void testIsLeft()
    {
        assertFalse(either.isLeft());
    }

    @Test
    public void testSwap()
    {
        final Either<Integer, Boolean> swapped = either.swap();
        assertTrue(swapped.isLeft());
        assertEquals(either.right().get(), swapped.left().get());
        assertEquals(ORIGINAL_VALUE, swapped.left().get());
    }

    @Test
    public void testMap()
    {
        assertEquals(String.valueOf(ORIGINAL_VALUE), either.fold(UtilityFunctions.bool2String, UtilityFunctions.int2String));
    }

    @Test
    public void testMapLeft()
    {
        assertTrue(either.left().map(UtilityFunctions.bool2String).isEmpty());
    }

    @Test
    public void testMapRight()
    {
        assertEquals(String.valueOf(ORIGINAL_VALUE), either.right().map(UtilityFunctions.int2String).get());
    }
}

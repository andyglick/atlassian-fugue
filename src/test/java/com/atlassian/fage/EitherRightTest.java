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
    public void right()
    {
        assertEquals(ORIGINAL_VALUE, either.right().get());
    }

    @Test
    public void left()
    {
        assertFalse(either.left().isDefined());
    }

    @Test
    public void isRight()
    {
        assertTrue(either.isRight());
    }

    @Test
    public void isLeft()
    {
        assertFalse(either.isLeft());
    }

    @Test
    public void swap()
    {
        final Either<Integer, Boolean> swapped = either.swap();
        assertTrue(swapped.isLeft());
        assertEquals(either.right().get(), swapped.left().get());
        assertEquals(ORIGINAL_VALUE, swapped.left().get());
    }

    @Test
    public void map()
    {
        assertEquals(String.valueOf(ORIGINAL_VALUE), either.fold(UtilityFunctions.bool2String, UtilityFunctions.int2String));
    }

    @Test
    public void mapLeft()
    {
        assertTrue(either.left().map(UtilityFunctions.bool2String).isEmpty());
    }

    @Test
    public void mapRight()
    {
        assertEquals(String.valueOf(ORIGINAL_VALUE), either.right().map(UtilityFunctions.int2String).get());
    }

    @Test
    public void toStringTest()
    {
        assertEquals("Either.Right(1)", either.toString());
    }
}

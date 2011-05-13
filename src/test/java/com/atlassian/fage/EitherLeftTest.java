package com.atlassian.fage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class EitherLeftTest
{
    private static final Boolean ORIGINAL_VALUE = true;
    final Either<Boolean, Integer> either = Either.left(ORIGINAL_VALUE);

    @Test
    public void left()
    {
        assertEquals(ORIGINAL_VALUE, either.left().get());
    }

    @Test
    public void right()
    {
        assertFalse(either.right().isDefined());
    }

    @Test
    public void isRight()
    {
        assertFalse(either.isRight());
    }

    @Test
    public void isLeft()
    {
        assertTrue(either.isLeft());
    }

    @Test
    public void swap()
    {
        final Either<Integer, Boolean> swapped = either.swap();
        assertTrue(swapped.isRight());
        assertEquals(either.left().get(), swapped.right().get());
        assertEquals(ORIGINAL_VALUE, swapped.right().get());
    }

    @Test
    public void map()
    {
        assertEquals(String.valueOf(ORIGINAL_VALUE), either.fold(UtilityFunctions.bool2String, UtilityFunctions.int2String));
    }

    @Test
    public void mapRight()
    {
        assertTrue(either.right().map(UtilityFunctions.int2String).isEmpty());
    }

    @Test
    public void mapLeft()
    {
        assertEquals(String.valueOf(ORIGINAL_VALUE), either.left().map(UtilityFunctions.bool2String).get());
    }

    @Test
    public void toStringTest()
    {
        assertEquals("Either.Left(true)", either.toString());
    }
}

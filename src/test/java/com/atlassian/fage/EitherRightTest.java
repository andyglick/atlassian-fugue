package com.atlassian.fage;

import static com.atlassian.fage.Either.right;
import static com.atlassian.fage.UtilityFunctions.bool2String;
import static com.atlassian.fage.UtilityFunctions.int2String;
import static java.lang.String.valueOf;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class EitherRightTest
{
    private static final Integer ORIGINAL_VALUE = 1;
    final Either<Boolean, Integer> either = right(ORIGINAL_VALUE);

    @Test
    public void rightGet()
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
        assertEquals(valueOf(ORIGINAL_VALUE), either.fold(bool2String, int2String));
    }

    @Test
    public void mapLeft()
    {
        assertTrue(either.left().map(bool2String).isEmpty());
    }

    @Test
    public void mapRight()
    {
        assertEquals(valueOf(ORIGINAL_VALUE), either.right().map(int2String).get());
    }

    @Test
    public void toStringTest()
    {
        assertEquals("Either.Right(1)", either.toString());
    }

    @Test
    public void hashCodeTest()
    {
        assertEquals(ORIGINAL_VALUE.hashCode(), either.hashCode());
    }

    @Test
    public void equalsItself()
    {
        assertTrue(either.equals(either));
    }

    @Test
    public void notEqualsNull()
    {
        assertFalse(either.equals(null));
    }
}
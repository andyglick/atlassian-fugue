package com.atlassian.fugue;

import static com.atlassian.fugue.Either.getOrThrow;
import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.UtilityFunctions.bool2String;
import static com.atlassian.fugue.UtilityFunctions.int2String;
import static java.lang.String.valueOf;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;

public class EitherLeftTest
{
    private static final Boolean ORIGINAL_VALUE = true;
    final Either<Boolean, Integer> either = left(ORIGINAL_VALUE);

    @Test
    public void leftGet()
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
        assertEquals(valueOf(ORIGINAL_VALUE), either.fold(bool2String, int2String));
    }

    @Test
    public void mapRight()
    {
        assertTrue(either.right().map(int2String).isEmpty());
    }

    @Test
    public void mapLeft()
    {
        assertEquals(valueOf(ORIGINAL_VALUE), either.left().map(bool2String).get());
    }

    @Test
    public void toStringTest()
    {
        assertEquals("Either.Left(true)", either.toString());
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

    @Test(expected = IOException.class)
    public void throwsException() throws IOException
    {
        final Either<IOException, String> either = left(new IOException());
        getOrThrow(either);
    }
}

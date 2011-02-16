package com.atlassian.fage;

import java.util.NoSuchElementException;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TestEither_TestLeft
{
    private static final Boolean ORIGINAL_VALUE = true;
    final Either<Boolean, Integer> either = Either.left(ORIGINAL_VALUE);
    
    @Test
    public void testLeft()
    {
        assertEquals(ORIGINAL_VALUE, either.left());
    }
    
    @Test (expected = NoSuchElementException.class)
    public void testRight()
    {
        either.right();
    }
    
    @Test
    public void testIsRight()
    {
        assertFalse(either.isRight());
    }
    
    @Test
    public void testIsLeft()
    {
        assertTrue(either.isLeft());
    }
    
    @Test
    public void testSwap()
    {
        Either<Integer, Boolean> swapped = either.swap();
        assertTrue(swapped.isRight());
        assertEquals(either.left(), swapped.right());
        assertEquals(ORIGINAL_VALUE, swapped.right());
    }
    
    @Test
    public void testMap()
    {
        String actual = either.map(EitherUtilityFunctions.bool2String, EitherUtilityFunctions.int2String);
        assertEquals(String.valueOf(ORIGINAL_VALUE), actual);
    }
    
    @Test (expected = UnsupportedOperationException.class)
    public void testMapRight()
    {
        either.mapRight(EitherUtilityFunctions.int2String);
    }
    
    @Test
    public void testMapLeft()
    {
        String actual = either.mapLeft(EitherUtilityFunctions.bool2String);
        assertEquals(String.valueOf(ORIGINAL_VALUE), actual);
    }
}

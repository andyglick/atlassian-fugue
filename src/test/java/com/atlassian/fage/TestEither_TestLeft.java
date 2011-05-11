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
        assertEquals(ORIGINAL_VALUE, either.left().get());
    }

    @Test
    public void testRight()
    {
        assertFalse(either.right().isSet());
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
        assertEquals(either.left().get(), swapped.right().get());
        assertEquals(ORIGINAL_VALUE, swapped.right().get());
    }
    
    @Test
    public void testMap()
    {
        String actual = either.fold(UtilityFunctions.bool2String, UtilityFunctions.int2String);
        assertEquals(String.valueOf(ORIGINAL_VALUE), actual);
    }
    
    @Test (expected = UnsupportedOperationException.class)
    public void testMapRight()
    {
        either.mapRight(UtilityFunctions.int2String);
    }
    
    @Test
    public void testMapLeft()
    {
        String actual = either.mapLeft(UtilityFunctions.bool2String);
        assertEquals(String.valueOf(ORIGINAL_VALUE), actual);
    }
}

package com.atlassian.fage;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.atlassian.fage.Option;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * EmptyIterator is a fairly simple class; these tests only exist to prevent future changes form breaking it.
 */
public class TestOption_TestEmptyIterator
{
    private Iterator iterator;

    @Before
    public void setup()
    {
        iterator = Option.EmptyIterator.instance();
    }
    
    @Test
    public void testHasNext()
    {
        assertFalse(iterator.hasNext());
    }

    @Test (expected = NoSuchElementException.class)
    public void testNext()
    {
        iterator.next();    
    }
    
    @Test (expected = UnsupportedOperationException.class)
    public void testRemove()
    {
        iterator.remove();
    }
    
}

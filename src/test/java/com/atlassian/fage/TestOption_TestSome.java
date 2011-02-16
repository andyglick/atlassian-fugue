package com.atlassian.fage;

import java.util.Iterator;

import com.atlassian.fage.Option;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class TestOption_TestSome
{
    private static final Integer ORIGINAL_VALUE = 1;
    private static final Integer NOT_IN_SOME = 3;
    Option<Integer> some;
    
    @Before
    public void setup()
    {
        some = new Option.Some<Integer>(ORIGINAL_VALUE);
    }
    
    @Test
    public void testGet()
    {
        Integer actual = some.get();
        assertEquals(ORIGINAL_VALUE, actual);
    }
    
    @Test
    public void testIsSet()
    {
        boolean actual = some.isSet();
        assertTrue(actual);
    }
    
    @Test
    public void testGetOrElse()
    {
        Integer actual = some.getOrElse(NOT_IN_SOME);
        assertEquals(ORIGINAL_VALUE, actual);
    }
    
    @Test (expected = NullPointerException.class)
    public void testMapForNull()
    {
        some.map(null);
    }
    
    @Test
    public void testMap()
    {
        Function<Integer, Integer> addOne = new Function<Integer, Integer>()
        {
            public Integer apply(Integer integer)
            {
                return integer + 1;
            }
        };

        Option<Integer> actual = some.map(addOne);
        assertEquals(new Integer(2), actual.get());
    }
    
    @Test (expected = NullPointerException.class)
    public void testFilterForNull()
    {
        some.filter(null);
    }
    
    @Test
    public void testPositiveFilter()
    {
        Predicate<Integer> predicateTrue = new Predicate<Integer>()
        {
            public boolean apply(Integer integer)
            {
                return true;
            }
        };
        Option<Integer> actual = some.filter(predicateTrue);
        assertEquals(ORIGINAL_VALUE, actual.get());
    }
    
    @Test
    public void testNegativeFilter()
    {
        Predicate<Integer> predicateFalse = new Predicate<Integer>()
        {
            public boolean apply(Integer integer)
            {
                return false;
            }
        };
        Option<Integer> actual = some.filter(predicateFalse);
        assertEquals(Option.<Integer>none(), actual);
    }
    
    @Test
    public void testIteratorHasNoNext()
    {
        Iterator<Integer> iterator = some.iterator();
        assertTrue(iterator.hasNext());
    }
    
    @Test
    public void testIteratorNext()
    {
        Iterator<Integer> iterator = some.iterator();
        Integer actual = iterator.next();
        assertEquals(ORIGINAL_VALUE, actual);
        assertFalse(iterator.hasNext());
    }
    
    @Test (expected = UnsupportedOperationException.class)
    public void testIteratorRemove()
    {
        Iterator<Integer> iterator = some.iterator();
        iterator.next();
        iterator.remove();
    }
}

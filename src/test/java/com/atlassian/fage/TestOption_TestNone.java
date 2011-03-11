package com.atlassian.fage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

public class TestOption_TestNone
{
    private final Option<Integer> none = Option.none();
    
    @Test (expected = NoSuchElementException.class)
    public void testGet()
    {
        none.get();
    }
    
    @Test
    public void testIsSet()
    {
        boolean actual = none.isSet();
        assertFalse(actual);
    }
    
    @Test
    public void testGetOrElse()
    {
        Integer orElse = none.getOrElse(1);
        assertEquals(new Integer(1), orElse);
    }
   
    @Test
    public void testMap()
    {
        Function<Integer, Integer> function = new Function<Integer, Integer>()
        {
            @Override
            public Integer apply(Integer input)
            {
                fail("None.map should not call the function.");
                return input;
            }
        };

        Option<Integer> result = none.map(function);
        assertSame(Option.None.class, result.getClass());
    }
    
    @Test (expected = NullPointerException.class)
    public void testNullFunctionForMap()
    {
        none.map(null);
    }
    
    @Test (expected = NullPointerException.class)
    public void testNullPredicateForFilter()
    {
        none.filter(null);
    }
    
    @Test
    public void testFilterReturnsEmpty()
    {
        Option<Integer> result = none.filter(Predicates.<Integer>alwaysTrue());
        assertEquals(Option.None.class, result.getClass());
    }

    @Test
    public void testSuperTypesPermittedOnFilter()
    {
        Option<ArrayList> opt = Option.none();
        Option<ArrayList> nopt = opt.filter(Predicates.<List>alwaysTrue());
        assertSame(opt, nopt);
    }

    @Test
    public void testSuperTypesPermittedOnMap()
    {
        Option<ArrayList> opt = Option.none();
        Option<Set> size = opt.map(new Function<List, Set>()
        {
            public Set apply(List list)
            {
                fail("This internal method should never get called.");
                return null;
            }
        });
        assertSame(opt, size);
    }

    // These tests are duplicated in TestEmptyIterator, but I've included them here to ensure
    // that None itself complies with the API.
    @Test
    public void testIteratorHasNoNext()
    {
        Iterator<Integer> iterator = none.iterator();
        assertFalse(iterator.hasNext());
    }
    
    @Test (expected = NoSuchElementException.class)
    public void testIteratorNext()
    {
        Iterator<Integer> iterator = none.iterator();
        iterator.next();        
    }
    
    @Test (expected = UnsupportedOperationException.class)
    public void testIteratorRemove()
    {
        Iterator<Integer> iterator = none.iterator();
        iterator.remove();
    }
    
}

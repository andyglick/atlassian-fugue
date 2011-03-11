package com.atlassian.fage;

import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class TestOption_TestSome
{
    private static final Integer ORIGINAL_VALUE = 1;
    private static final Integer NOT_IN_SOME = 3;
    Option<Integer> some = new Option.Some<Integer>(ORIGINAL_VALUE);
    
    @Test
    public void testGet()
    {
        assertEquals(ORIGINAL_VALUE, some.get());
    }
    
    @Test
    public void testIsSet()
    {
        assertTrue(some.isSet());
    }
    
    @Test
    public void testGetOrElse()
    {
        assertEquals(ORIGINAL_VALUE, some.getOrElse(NOT_IN_SOME));
    }
    
    @Test (expected = NullPointerException.class)
    public void testMapForNull()
    {
        some.map(null);
    }
    
    @Test
    public void testMap()
    {
        Option<Integer> actual = some.map(UtilityFunctions.addOne);
        assertEquals(new Integer(2), actual.get());
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
        ArrayList<Number> list = new ArrayList<Number>();
        list.add(1);
        list.add(2);
        Option<ArrayList<Number>> opt = Option.get(list);
        Option<Set<Number>> set = opt.map(new Function<List<Number>, Set<Number>>()
        {
            public Set apply(List<Number> list)
            {
                Set<Number> set = new HashSet<Number>();
                set.addAll(list);
                return set;
            }
        });
        assertSame(opt.get().size(), set.get().size());
    }

    @Test (expected = NullPointerException.class)
    public void testFilterForNull()
    {
        some.filter(null);
    }
    
    @Test
    public void testPositiveFilter()
    {
        Option<Integer> actual = some.filter(Predicates.<Integer>alwaysTrue());
        assertEquals(ORIGINAL_VALUE, actual.get());
    }
    
    @Test
    public void testNegativeFilter()
    {
        Option<Integer> actual = some.filter(Predicates.<Integer>alwaysFalse());
        assertEquals(Option.<Integer>none(), actual);
    }
    
    @Test
    public void testIteratorHasNoNext()
    {
        assertTrue(some.iterator().hasNext());
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

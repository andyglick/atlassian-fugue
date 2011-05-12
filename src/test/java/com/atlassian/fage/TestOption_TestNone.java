package com.atlassian.fage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class TestOption_TestNone
{
    private final Option<Integer> none = Option.none();

    @Test(expected = NoSuchElementException.class)
    public void testGet()
    {
        none.get();
    }

    @Test
    public void testIsSet()
    {
        assertFalse(none.isDefined());
    }

    @Test
    public void testGetOrElse()
    {
        assertEquals(new Integer(1), none.getOrElse(1));
    }

    @Test
    public void testMap()
    {
        final Function<Integer, Integer> function = new Function<Integer, Integer>()
        {
            @Override
            public Integer apply(final Integer input)
            {
                fail("None.map should not call the function.");
                return input;
            }
        };

        assertTrue(none.map(function).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void testNullFunctionForMap()
    {
        none.map(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPredicateForFilter()
    {
        none.filter(null);
    }

    @Test
    public void testFilterTrueReturnsEmpty()
    {
        assertTrue(none.filter(Predicates.<Integer> alwaysTrue()).isEmpty());
    }

    @Test
    public void testFilterFalseReturnsEmpty()
    {
        assertTrue(none.filter(Predicates.<Integer> alwaysFalse()).isEmpty());
    }

    @Test
    public void testSuperTypesPermittedOnFilter()
    {
        final Option<ArrayList<?>> opt = Option.none();
        final Option<ArrayList<?>> nopt = opt.filter(Predicates.<List<?>> alwaysTrue());
        assertSame(opt, nopt);
    }

    @Test
    public void testSuperTypesPermittedOnMap()
    {
        final Option<ArrayList<?>> opt = Option.none();
        final Option<Set<?>> size = opt.map(new Function<List<?>, Set<?>>()
        {
            public Set<?> apply(final List<?> list)
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
        final Iterator<Integer> iterator = none.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorNext()
    {
        final Iterator<Integer> iterator = none.iterator();
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove()
    {
        final Iterator<Integer> iterator = none.iterator();
        iterator.remove();
    }

}

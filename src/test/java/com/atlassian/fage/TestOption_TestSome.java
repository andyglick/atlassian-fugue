package com.atlassian.fage;

import static com.atlassian.fage.Option.option;
import static com.atlassian.fage.Option.some;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TestOption_TestSome
{
    private static final Integer ORIGINAL_VALUE = 1;
    private static final Integer NOT_IN_SOME = 3;
    Option<Integer> some = some(ORIGINAL_VALUE);

    @Test
    public void testGet()
    {
        assertEquals(ORIGINAL_VALUE, some.get());
    }

    @Test
    public void testIsSet()
    {
        assertTrue(some.isDefined());
    }

    @Test
    public void testGetOrElse()
    {
        assertEquals(ORIGINAL_VALUE, some.getOrElse(NOT_IN_SOME));
    }

    @Test(expected = NullPointerException.class)
    public void testMapForNull()
    {
        some.map(null);
    }

    @Test
    public void testMap()
    {
        final Option<Integer> actual = some.map(UtilityFunctions.addOne);
        assertEquals(new Integer(2), actual.get());
    }

    @Test
    public void testSuperTypesPermittedOnFilter()
    {
        final ArrayList<Number> list = new ArrayList<Number>();
        list.add(1);
        list.add(2);
        final Option<ArrayList<Number>> opt = option(list);
        final Option<ArrayList<Number>> nopt = opt.filter(Predicates.<List<Number>> alwaysTrue());
        assertSame(opt, nopt);
    }

    @Test
    public void testSuperTypesPermittedOnMap()
    {
        final ArrayList<Number> list = new ArrayList<Number>();
        list.add(1);
        list.add(2);
        final Option<ArrayList<Number>> opt = option(list);
        final Option<Set<Number>> set = opt.map(new Function<List<Number>, Set<Number>>()
        {
            public Set<Number> apply(final List<Number> list)
            {
                final Set<Number> set = new HashSet<Number>();
                set.addAll(list);
                return set;
            }
        });
        assertSame(opt.get().size(), set.get().size());
    }

    @Test(expected = NullPointerException.class)
    public void testFilterForNull()
    {
        some.filter(null);
    }

    @Test
    public void testPositiveFilter()
    {
        final Option<Integer> actual = some.filter(Predicates.<Integer> alwaysTrue());
        assertEquals(ORIGINAL_VALUE, actual.get());
    }

    @Test
    public void testNegativeFilter()
    {
        final Option<Integer> actual = some.filter(Predicates.<Integer> alwaysFalse());
        assertEquals(Option.<Integer> none(), actual);
    }

    @Test
    public void testIteratorHasNoNext()
    {
        assertTrue(some.iterator().hasNext());
    }

    @Test
    public void testIteratorNext()
    {
        final Iterator<Integer> iterator = some.iterator();
        final Integer actual = iterator.next();
        assertEquals(ORIGINAL_VALUE, actual);
        assertFalse(iterator.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove()
    {
        final Iterator<Integer> iterator = some.iterator();
        iterator.next();
        iterator.remove();
    }
}

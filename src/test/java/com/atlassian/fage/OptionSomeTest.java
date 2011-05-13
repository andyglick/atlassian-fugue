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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OptionSomeTest
{
    private static final Integer ORIGINAL_VALUE = 1;
    private static final Integer NOT_IN_SOME = 3;
    Option<Integer> some = some(ORIGINAL_VALUE);

    @Test
    public void get()
    {
        assertEquals(ORIGINAL_VALUE, some.get());
    }

    @Test
    public void isSet()
    {
        assertTrue(some.isDefined());
    }

    @Test
    public void getOrElse()
    {
        assertEquals(ORIGINAL_VALUE, some.getOrElse(NOT_IN_SOME));
    }

    @Test
    public void getOrNull()
    {
        assertEquals(ORIGINAL_VALUE, some.getOrNull());
    }

    @Test(expected = NullPointerException.class)
    public void mapForNull()
    {
        some.map(null);
    }

    @Test
    public void map()
    {
        final Option<Integer> actual = some.map(UtilityFunctions.addOne);
        assertEquals(new Integer(2), actual.get());
    }

    @Test
    public void superTypesPermittedOnFilter()
    {
        final ArrayList<Integer> list = Lists.newArrayList(1, 2);
        final Option<ArrayList<Integer>> option = option(list);
        final Option<ArrayList<Integer>> nopt = option.filter(Predicates.<List<Integer>> alwaysTrue());
        assertSame(option, nopt);
    }

    @Test
    public void superTypesPermittedOnMap()
    {
        final ArrayList<Integer> list = Lists.newArrayList(1, 2);
        final Option<ArrayList<Integer>> option = option(list);
        final Option<Set<Number>> set = option.map(new Function<List<Integer>, Set<Number>>()
        {
            public Set<Number> apply(final List<Integer> list)
            {
                return Sets.<Number> newHashSet(list);
            }
        });
        assertSame(option.get().size(), set.get().size());
    }

    @Test(expected = NullPointerException.class)
    public void filterForNull()
    {
        some.filter(null);
    }

    @Test
    public void positiveFilter()
    {
        final Option<Integer> actual = some.filter(Predicates.<Integer> alwaysTrue());
        assertEquals(ORIGINAL_VALUE, actual.get());
    }

    @Test
    public void negativeFilter()
    {
        final Option<Integer> actual = some.filter(Predicates.<Integer> alwaysFalse());
        assertEquals(Option.<Integer> none(), actual);
    }

    @Test
    public void iteratorHasNext()
    {
        assertTrue(some.iterator().hasNext());
    }

    @Test
    public void iteratorNext()
    {
        final Iterator<Integer> iterator = some.iterator();
        final Integer actual = iterator.next();
        assertEquals(ORIGINAL_VALUE, actual);
        assertFalse(iterator.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void iteratorImmutable()
    {
        final Iterator<Integer> iterator = some.iterator();
        iterator.next();
        iterator.remove();
    }
}

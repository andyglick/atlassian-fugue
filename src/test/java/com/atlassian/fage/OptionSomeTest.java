package com.atlassian.fage;

import static com.atlassian.fage.Option.option;
import static com.atlassian.fage.Option.some;
import static com.atlassian.fage.Suppliers.ofInstance;
import static com.atlassian.fage.UtilityFunctions.addOne;
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
        assertEquals(new Integer(2), some.map(addOne).get());
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
        assertEquals(ORIGINAL_VALUE, some.filter(Predicates.<Integer> alwaysTrue()).get());
    }

    @Test
    public void negativeFilter()
    {
        assertEquals(Option.<Integer> none(), some.filter(Predicates.<Integer> alwaysFalse()));
    }

    @Test
    public void existsTrueReturnsTrue()
    {
        assertTrue(some.exists(Predicates.<Integer> alwaysTrue()));
    }

    @Test
    public void existsFalseReturnsFalse()
    {
        assertFalse(some.exists(Predicates.<Integer> alwaysFalse()));
    }

    @Test
    public void toLeftReturnsLeft()
    {
        assertTrue(some.toLeft(ofInstance("")).isLeft());
    }

    @Test
    public void toRightReturnsRight()
    {
        assertTrue(some.toRight(ofInstance("")).isRight());
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

    @Test
    public void toStringTest()
    {
        assertEquals("some(1)", some.toString());
    }

    @Test
    public void equalsItself()
    {
        assertTrue(some.equals(some));
    }

    @Test
    public void notEqualsNull()
    {
        assertFalse(some.equals(null));
    }
}

package com.atlassian.fage;

import static com.atlassian.fage.Option.option;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Iterator;

public class TestOption
{
    @Test
    public void testGetNull()
    {
        final Option<Object> objectOption = option(null);
        assertSame(Option.<Object> none(), objectOption);
    }

    @Test
    public void testGet()
    {
        final Option<String> option = option("Winter.");
        final String actual = option.get();
        assertEquals("Winter.", actual);
    }

    @Test
    public void testIdentity()
    {
        assertSame(Option.none(), Option.none());
    }

    @Test
    public void testFindFindsFirst()
    {
        final Iterable<Option<Integer>> options = ImmutableList.of(option((Integer) null), option(2), option((Integer) null), option(4));
        assertEquals(new Integer(2), Option.find(options).get());
    }

    @Test
    public void testFindFindsOneSingleton()
    {
        final Iterable<Option<Integer>> options = ImmutableList.of(option(3));
        assertEquals(new Integer(3), Option.find(options).get());
    }

    @Test
    public void testFindFindsNone()
    {
        final Iterable<Option<Integer>> options = ImmutableList.of(Option.<Integer> option((Integer) null), option((Integer) null),
            option((Integer) null), option((Integer) null));
        assertFalse(Option.find(options).isDefined());
    }

    @Test
    public void testFindFindsNoneSingleton()
    {
        final Iterable<Option<Integer>> options = ImmutableList.of(Option.<Integer> option((Integer) null));
        assertFalse(Option.find(options).isDefined());
    }

    @Test
    public void testFilterFindsTwo()
    {
        final Iterable<Option<Integer>> options = ImmutableList.of(option((Integer) null), option(2), option((Integer) null), option(4));
        final Iterable<Option<Integer>> filtered = Option.filterNone(options);
        assertEquals(2, Iterables.size(filtered));
        final Iterator<Option<Integer>> it = filtered.iterator();
        assertEquals(new Integer(2), it.next().get());
        assertEquals(new Integer(4), it.next().get());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFilterFindsNone()
    {
        final Iterable<Option<Integer>> options = ImmutableList.of(Option.<Integer> option((Integer) null), option((Integer) null),
            option((Integer) null), option((Integer) null));
        assertEquals(0, Iterables.size(Option.filterNone(options)));
    }
}

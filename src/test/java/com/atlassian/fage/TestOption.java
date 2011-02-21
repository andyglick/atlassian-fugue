package com.atlassian.fage;

import org.junit.Test;
import com.google.common.collect.ImmutableList;

import static com.atlassian.fage.Option.find;
import static com.atlassian.fage.Option.get;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class TestOption
{
    @Test
    public void testGetNull()
    {
        Option<Object> objectOption = Option.get(null);
        assertSame(Option.<Object>none(), objectOption);
    }
        
    @Test
    public void testGet()
    {
        Option<String> option = Option.get("Winter.");
        String actual = option.get();
        assertEquals("Winter.", actual);
    }
    
    @Test
    public void testNoneIdempotency()
    {
        assertTrue(Option.none() == Option.none());
    }

	@Test
	public void testFindFindsFirst()
	{
	    Iterable<Option<Integer>> options = ImmutableList.of(get((Integer) null), get(2), get((Integer) null), get(4));
	    assertEquals(new Integer(2), Option.find(options).get());
	}

	@Test
	public void testFindFindsNone()
	{
	    Iterable<Option<Integer>> options = ImmutableList.of(Option.<Integer> get((Integer) null), get((Integer) null), get((Integer) null), get((Integer) null));
	    assertFalse(Option.find(options).isSet());
	}

	@Test
	public void testFindFindsNoneSingleton()
	{
	    Iterable<Option<Integer>> options = ImmutableList.of(Option.<Integer> get((Integer) null));
	    assertFalse(Option.find(options).isSet());
	}

	@Test
	public void testFindFindsOneSingleton()
	{
	    Iterable<Option<Integer>> options = ImmutableList.of(get(3));
	    assertEquals(new Integer(3), Option.find(options).get());
	}
}

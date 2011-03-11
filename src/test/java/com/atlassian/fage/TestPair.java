package com.atlassian.fage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPair
{
    @Test(expected = NullPointerException.class) public void testNullLeft()
    {
        new Pair<String, String>(null, "");
    }
    
    @Test(expected = NullPointerException.class) public void testNullRight()
    {
        new Pair<String, String>("", null);
    }
    
    @Test public void testLeft()
    {
        Pair<String, String> pair = new Pair<String, String>("left", "right");
        assertEquals("left", pair.left());
    }
    
    @Test public void testRight()
    {
        Pair<String, String> pair = new Pair<String, String>("left", "right");
        assertEquals("right", pair.right());
    }

    @Test public void testToString()
    {
        Pair<String, Integer> pair = new Pair<String, Integer>("hello", 4);
        String actual = pair.toString();
        assertEquals("Pair: (a: {hello}, b: {4})", actual);
    }

    @Test public void testHashCode()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        int actual = pair.hashCode();
        assertEquals(65539, actual);
    }

    @Test public void testNotEqualToNull()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        assertFalse(pair.equals(null));
    }

    @Test public void testEqualToSelf()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        assertTrue(pair.equals(pair));
    }

    @Test public void testNotEqualToArbitraryObject()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        assertFalse(pair.equals(new Object()));
    }

    @Test public void testNotEqualLeft()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        assertFalse(pair.equals(new Pair<Integer, Integer>(0,3)));
    }

    @Test public void testNotEqualRight()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        assertFalse(pair.equals(new Pair<Integer, Integer>(1,0)));
    }

    @Test public void testEqualsSameValue()
    {
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(1,3);
        assertTrue(pair.equals(new Pair<Integer, Integer>(1,3)));
    }
}

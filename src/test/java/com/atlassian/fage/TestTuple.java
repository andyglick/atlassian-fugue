package com.atlassian.fage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTuple
{
    @Test(expected = NullPointerException.class) public void testNullLeft()
    {
        new Tuple<String, String>(null, "");
    }
    
    @Test(expected = NullPointerException.class) public void testNullRight()
    {
        new Tuple<String, String>("", null);
    }
    
    @Test public void testLeft()
    {
        Tuple<String, String> tuple = new Tuple<String, String>("left", "right");
        assertEquals("left", tuple.left());
    }
    
    @Test public void testRight()
    {
        Tuple<String, String> tuple = new Tuple<String, String>("left", "right");
        assertEquals("right", tuple.right());
    }
    
    @Test public void testCons()
    {
        Tuple<String, String> tuple = new Tuple<String, String>("left", "right");
        Tuple<Integer, Tuple<String,String>> actual = tuple.cons(2);
        
        assertEquals(new Integer(2), actual.left());
        assertEquals(tuple, actual.right());
        assertEquals("left", actual.right().left());
        assertEquals("right", actual.right().right());
    }
}

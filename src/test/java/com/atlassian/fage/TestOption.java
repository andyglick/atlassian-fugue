package com.atlassian.fage;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
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
}

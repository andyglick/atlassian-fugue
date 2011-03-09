package com.atlassian.fage;

import com.google.common.base.Function;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestFold
{
    @Test public void testF2FoldBasic()
    {
        Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>()
        {
            @Override
            public Integer apply(Integer arg1, Integer arg2)
            {
                return arg1 + arg2;
            }
        };
        List<Integer> ints = Arrays.asList(3, 4, 5);
        Integer actual = Functions.fold(add, 2, ints);
        assertEquals(new Integer(14), actual);
    }
    
    @Test public void testF2FoldTypes()
    {
        Function2<String, Integer, String> displayIterable = new Function2<String, Integer, String>()
        {
            @Override
            public String apply(String arg1, Integer arg2)
            {
                return arg1 + "  " + arg2;
            }
        };
        
        List<Integer> ints = Arrays.asList(12, 15, 20);
        String actual = Functions.fold(displayIterable, "Iterable:", ints);
        
        assertEquals("Iterable:  12  15  20", actual);
    }

    @Test public void testF1FoldBasic()
    {
        Function<Tuple<Integer, Integer>, Integer> add = new Function<Tuple<Integer, Integer>, Integer>()
        {
            @Override
            public Integer apply(Tuple<Integer, Integer> arg)
            {
                return arg.left() + arg.right();
            }
        };
        List<Integer> ints = Arrays.asList(3, 4, 5);
        Integer actual = Functions.fold(add, 2, ints);
        assertEquals(new Integer(14), actual);
    }

    @Test public void testF1FoldTypes()
    {
        Function<Tuple<String, Integer>, String> displayIterable = new Function<Tuple<String, Integer>, String>()
        {
            @Override
            public String apply(Tuple<String, Integer> t)
            {
                return t.left() + "  " + t.right();
            };
        };

        List<Integer> ints = Arrays.asList(12, 15, 20);
        String actual = Functions.fold(displayIterable, "Iterable:", ints);

        assertEquals("Iterable:  12  15  20", actual);
    }
}

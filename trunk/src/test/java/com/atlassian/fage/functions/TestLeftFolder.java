package com.atlassian.fage.functions;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TestLeftFolder
{

    @Test
    public void testBasicLeftFold()
    {
        int expected = 10;
        List<Integer> integers = Arrays.asList(1,2,3,4);

        Function2Arg<Integer, Integer, Integer> add = new Function2Arg<Integer, Integer, Integer>() {

            @Override
            public Integer apply(Integer arg1, Integer arg2)
            {
                return arg1 + arg2;
            }

        };

        Integer actual = new LeftFolder<Integer, Integer>(0, add).apply(integers);
        assertEquals(expected, actual.intValue());
    }
    
    @Test public void testNullArgs()
    {
        Function2Arg<Boolean, Object, Boolean> hasAnyNulls = new Function2Arg<Boolean, Object, Boolean>() {

            @Override
            public Boolean apply(final Boolean b, final Object o)
            {
                return b || o == null;
            }
        };
        
        List<Object> nulls = Arrays.asList(new Object(), null, new Object());

        boolean actual = new LeftFolder<Object, Boolean>(false, hasAnyNulls).apply(nulls);
        assertEquals(actual, true);
    }
    
    @Test
    public void testLeftOrdering()
    {
        String expected = "initial" + "1" + "2" + "3";
        
        List<String> strings = Arrays.asList("1", "2", "3");

        String result = new LeftFolder<String, String>("initial", new UtilityFunctions.Concatenate2()).apply(strings);
        
        assertEquals(expected, result);
    }
    
    @Test
    public void testLeftUncurry()
    {
        String expected = "initial" + "1" + "2" + "3";
        
        List<Integer> ints = Arrays.asList(1, 2, 3);

        Function<Integer, String> convertor = new UtilityFunctions.StringToInteger();

        String result = new LeftFolder<Integer, String>("initial", new UncurriedFunction<String, Integer, String>(convertor, new UtilityFunctions.ConcatenateGenerator())).apply(ints);
        
        assertEquals(expected, result);
    }    
}

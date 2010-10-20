package com.atlassian.fage.functions;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TestFold
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
        
        Integer actual = Fold.left(add, 0, integers);
        assertEquals(expected, actual.intValue());
    }
    
    @Test public void testNullArgs()
    {
        Function2Arg<Boolean, Boolean, Object> hasAnyNulls = new Function2Arg<Boolean, Boolean, Object>() {

            @Override
            public Boolean apply(Boolean arg1, Object arg2)
            {
                return arg1 || arg2 == null; 
            }
        };
        
        List<Object> nulls = Arrays.asList(new Object(), null, new Object());
        
        boolean actual = Fold.left(hasAnyNulls, false, nulls);
        assertEquals(actual, true);
    }
    
    @Test
    public void testLeftOrdering()
    {
        String expected = "initial" + "1" + "2" + "3";
        
        List<String> strings = Arrays.asList("1", "2", "3");
        
        Function2Arg<String, String, String> cat = new Function2Arg<String, String, String>() {
            @Override
            public String apply(String arg1, String arg2)
            {
                return arg1 + arg2;
            }
        };
        
        String result = Fold.left(cat, "initial", strings);
        
        assertEquals(expected, result);
    }
    

    @Test
    public void testRightOrdering()
    {
        String expected = "initial" + "3" + "2" + "1";
        
        List<String> strings = Arrays.asList("1", "2", "3");
        
        Function2Arg<String, String, String> cat = new Function2Arg<String, String, String>() {
            @Override
            public String apply(String arg1, String arg2)
            {
                return arg1 + arg2;
            }
        };
        
        String result = Fold.right(cat, "initial", strings);
        
        assertEquals(expected, result);
    }

    
    @Test
    public void testRightUncurry()
    {
        String expected = "initial" + "3" + "2" + "1";
        
        List<Integer> ints = Arrays.asList(1, 2, 3);

        Function<Integer, String> convertor = new Function<Integer, String>() {

            @Override
            public String apply(Integer integer)
            {
                return String.valueOf(integer);
            }
        };


        Function<String, Function<String, String>> combinerGenerator = new Function<String, Function<String, String>>() {

            @Override
            public Function<String, String> apply(final String arg1)
            {
                return new Concatenate(arg1);
            }
        };
        String result = Fold.right(convertor, combinerGenerator, "initial", ints);
        
        assertEquals(expected, result);
    }
    
    @Test
    public void testLeftUncurry()
    {
        String expected = "initial" + "1" + "2" + "3";
        
        List<Integer> ints = Arrays.asList(1, 2, 3);

        Function<Integer, String> convertor = new Function<Integer, String>() {

            @Override
            public String apply(Integer integer)
            {
                return String.valueOf(integer);
            }
        };


        Function<String, Function<String, String>> combinerGenerator = new Function<String, Function<String, String>>() {

            @Override
            public Function<String, String> apply(final String arg1)
            {
                return new Concatenate(arg1);
            }
        };
        String result = Fold.left(convertor, combinerGenerator, "initial", ints);
        
        assertEquals(expected, result);
    }

    private static class Concatenate implements Function<String, String>
    {
        private final String arg1;

        public Concatenate(final String arg1) {this.arg1 = arg1;}

        @Override
        public String apply(final String arg2)
        {
            return arg1 + arg2;
        }
    }
}

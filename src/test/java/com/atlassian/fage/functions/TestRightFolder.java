package com.atlassian.fage.functions;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TestRightFolder
{
    

    @Test
    public void testRightOrdering()
    {
        String expected = "initial" + "3" + "2" + "1";
        
        List<String> strings = Arrays.asList("1", "2", "3");

        String result = new RightFolder<String, String>("initial", new UtilityFunctions.Concatenate()).apply(strings);
        
        assertEquals(expected, result);
    }

    
    @Test
    public void testRightUncurry()
    {
        String expected = "initial" + "3" + "2" + "1";
        
        List<Integer> ints = Arrays.asList(1, 2, 3);

        Function<Integer, String> convertor = new UtilityFunctions.StringToInteger();

        String result = new RightFolder<Integer, String>("initial", new UncurriedFunction<String, Integer, String>(convertor, new UtilityFunctions.ConcatenateGenerator())).apply(ints);
        
        assertEquals(expected, result);
    }
}

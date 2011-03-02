package com.atlassian.fage;

import com.google.common.base.Function;

public class UtilityFunctions
{
    public static Function<Integer, Integer> addOne = new Function<Integer, Integer>()
    {
        public Integer apply(Integer integer)
        {
            return integer + 1;
        }
    };
    
    public static Function<Boolean, String> bool2String = new Function<Boolean, String>()
    {
        public String apply(Boolean b)
        {
            return String.valueOf(b);
        }
    };
    public static Function<Integer, String> int2String = new Function<Integer, String>()
    {
        public String apply(Integer i)
        {
            return String.valueOf(i);
        }
    };
}

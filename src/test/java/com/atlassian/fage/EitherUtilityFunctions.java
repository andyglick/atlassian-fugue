package com.atlassian.fage;

import com.google.common.base.Function;

public class EitherUtilityFunctions
{
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

package com.atlassian.fage;

import com.google.common.base.Function;

public class UtilityFunctions
{
    public static class StringToInteger implements Function<Integer, String>
    {
        @Override
        public String apply(Integer integer)
        {
            return String.valueOf(integer);
        }
    }

    public static class ConcatenateGenerator implements Function<String, Function<String, String>>
    {
        @Override
        public Function<String, String> apply(final String arg1)
        {
            return new GeneratedConcatenate(arg1);
        }

        static class GeneratedConcatenate implements Function<String, String>
        {
            private final String arg1;

            public GeneratedConcatenate(final String arg1) {this.arg1 = arg1;}

            @Override
            public String apply(final String arg2)
            {
                return arg1 + arg2;
            }
        }
    }
    
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

package com.atlassian.fage.functions;

import com.google.common.base.Function;

public class UtilityFunctions
{
    static class StringToInteger implements Function<Integer, String>
    {
        @Override
        public String apply(Integer integer)
        {
            return String.valueOf(integer);
        }
    }

    static class ConcatenateGenerator implements Function<String, Function<String, String>>
    {
        @Override
        public Function<String, String> apply(final String arg1)
        {
            return new Concatenate(arg1);
        }

        static class Concatenate implements Function<String, String>
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

    static class Concatenate2 implements Function2Arg<String, String, String>
    {
        @Override
        public String apply(String arg1, String arg2)
        {
            return arg1 + arg2;
        }
    }
}

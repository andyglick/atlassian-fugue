package com.atlassian.fage;

import com.google.common.base.Function;

public class OptionUtilityFunctions
{
    static Function<Integer, Integer> addOne = new Function<Integer, Integer>()
    {
        public Integer apply(Integer integer)
        {
            return integer + 1;
        }
    };
}

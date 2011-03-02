package com.atlassian.fage;

public interface Function2<ParamType1, ParamType2, ReturnType>
{
    ReturnType apply(ParamType1 arg1, ParamType2 arg2);
}

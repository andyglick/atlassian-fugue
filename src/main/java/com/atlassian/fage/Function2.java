package com.atlassian.fage;

public interface Function2<F1, F2, T>
{
    T apply(F1 arg1, F2 arg2);
}

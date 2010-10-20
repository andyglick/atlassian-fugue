package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;

/**
 * @param <T> The return value
 * @param <U> The type of the first parameter
 * @param <V> the type of the second parameter
 */
@Beta
public interface Function2Arg<T, U, V>
{
    T apply(U arg1, V arg2);
}

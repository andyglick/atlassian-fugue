package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;

/**
 * @param <A> The type of the first parameter
 * @param <B> the type of the second parameter
 * @param <R> The return value
 */
@Beta
public interface Function2Arg<A, B, R>
{
    R apply(A arg1, B arg2);
}

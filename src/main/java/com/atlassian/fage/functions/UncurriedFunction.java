package com.atlassian.fage.functions;

import com.google.common.base.Function;

/**
 * This class composes two functions in a way that is compatible with Fold.
 * @param <U>
 * @param <V>
 */
class UncurriedFunction<U, V> implements Function2Arg<U, U, V>
{
    private final Function<V, U> convertor;
    private final Function<U, Function<U, U>> generator;

    /**
     * The generator will almost always take the form
     * <pre><code>
     * 
     * class Generator 
     * 
     * class GeneratedFunction implements Function<U, U>
     * {
     *     private final U arg1;
     *  
     *     public GeneratedFunction(final U arg1) {this.arg1 = arg1;}
     *
     *     public U apply(final U arg2)
     *     {
     *         // Implement this part.
     *         // Integer addition would use "return arg1 + arg2;"
     *         // 
     *     }
     * }
     * </code></pre>
     * 
     * @param convertor creates Us out of Vs
     * @param generator generates functions which perform an operation on two Us
     */
    public UncurriedFunction(final Function<V, U> convertor, final Function<U, Function<U, U>> generator)
    {
        this.convertor = convertor;
        this.generator = generator;
    }

    @Override
    public U apply(U arg1, V arg2)
    {
        U u = convertor.apply(arg2);
        
        return generator.apply(arg1).apply(u);
    }
}

package com.atlassian.fage.functions;

import com.google.common.base.Function;

/**
 * This class composes two functions in a way that is compatible with Fold.
 * @param <B> The type of the arg that will be converted into a result type
 * @param <R> The type of the result
 */
class UncurriedFunction<A, B, R> implements Function2Arg<A, B, R>
{
    private final Function<B, R> convertor;
    private final Function<A, Function<R, R>> generator;

    /**
     * The typical form of a generator is as follows:
     * <pre><code>
     * 
     * class ExampleGenerator implements Function<Integer, Function<String, String>> {
     *     public Function<String, String> apply(final Integer a) {
     *         return new ExampleGeneratedFunction(a);
     *     }
     *  
     *     class ExampleGeneratedFunction implements Function<String, String> {
     *         private final Integer a;
     *
     *         public ExampleGeneratedFunction(final Integer a) {
     *             this.a = a;
     *         }
     *
     *         public String apply(final String r) {
     *            return r + "," + String.valueOf(a); // action code here
     *         }
     *     }
     * }
     * </code></pre>
     * 
     * @param convertor creates Us out of Vs
     * @param generator generates functions which perform an operation on two Us
     */
    public UncurriedFunction(final Function<B, R> convertor, final Function<A, Function<R, R>> generator)
    {
        this.convertor = convertor;
        this.generator = generator;
    }

    @Override
    public R apply(A arg1, B arg2)
    {
        return generator.apply(arg1).apply(convertor.apply(arg2));
    }
}

package com.atlassian.fage.functions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * This class composes two Guava Functions in a way that is compatible with Fold.
 * 
 * @param <A> The type of one arg that will be converted into a result type
 * @param <B> The type of another arg that will be converted into a result type
 * @param <R> The type of the result
 */
@Beta
public class UncurriedFunction<A, B, R> implements Function2Arg<A, B, R>
{
    private final Function<B, R> convertor;
    private final Function<A, Function<R, R>> generator;

    /**
     * This unwieldy setup can be used when your functions need to interact with existing guava functions. If that is
     * not required, use of Function2Arg is recommended instead. 
     * 
     * The typical form of a convertor is as follows:
     * <pre><code>
     * class ExampleConvertor implements Function<Object, Integer> {
     *     public Integer apply(final Object o)
     *     {
     *         return String.valueOf(o).length();
     *     }
     * }
     * </code></pre>
     * 
     * 
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
     * Although it is possible to uncurry two functions B-> R and A -> R -> R, the most typical use (e.g. in a fold)
     * will use the same type for A and R.
     * 
     * @param convertor creates Rs out of Bs
     * @param generator generates functions which perform an operation on an R and an A to produce an R
     * @see com.atlassian.fage.functions.Fold#uncurry
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

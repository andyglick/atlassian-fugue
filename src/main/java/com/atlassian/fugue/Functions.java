package com.atlassian.fugue;

import static com.atlassian.fugue.Either.left;
import static com.atlassian.fugue.Either.right;
import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapMaker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Functions
{
    private Functions()
    {}

    public static <F, T> T fold(final Function2<T, F, T> f, final T zero, final Iterable<F> elements)
    {
        T currentValue = zero;
        for (final F element : elements)
        {
            currentValue = f.apply(currentValue, element);
        }
        return currentValue;
    }

    public static <F, T> T fold(final Function<Pair<T, F>, T> f, final T zero, final Iterable<F> elements)
    {
        return fold(new Function2<T, F, T>()
        {
            @Override
            public T apply(final T arg1, final F arg2)
            {
                return f.apply(new Pair<T, F>(arg1, arg2));
            }
        }, zero, elements);
    }

    /**
     * Get the value from a supplier.
     * 
     * @param <T> the type returned, note the Supplier can be covariant.
     * @return a function that extracts the value from a supplier
     */
    public static <T> Function<Supplier<? extends T>, T> fromSupplier()
    {
        return new ValueExtractor<T>();
    }

    private static class ValueExtractor<T> implements Function<Supplier<? extends T>, T>
    {
        public T apply(final Supplier<? extends T> supplier)
        {
            return supplier.get();
        }
    }

    /**
     * Function that can be used to ignore any RuntimeExceptions that a
     * {@link Supplier} may produce and return null instead.
     * 
     * @param <T> the result type
     * @return a Function that transforms an exception into a null
     */
    public static <T> Function<Supplier<? extends T>, Supplier<T>> ignoreExceptions()
    {
        return new ExceptionIgnorer<T>();
    }

    static class ExceptionIgnorer<T> implements Function<Supplier<? extends T>, Supplier<T>>
    {
        public Supplier<T> apply(final Supplier<? extends T> from)
        {
            return new IgnoreAndReturnNull<T>(from);
        }
    }

    static class IgnoreAndReturnNull<T> implements Supplier<T>
    {
        private final Supplier<? extends T> delegate;

        IgnoreAndReturnNull(final Supplier<? extends T> delegate)
        {
            this.delegate = checkNotNull(delegate);
        }

        public T get()
        {
            try
            {
                return delegate.get();
            }
            catch (final RuntimeException ignore)
            {
                return null;
            }
        }
    }

    public static <T> Function<T, List<T>> singletonList(final Class<T> c)
    {
        return new SingletonList<T>();
    }

    private static final class SingletonList<T> implements Function<T, List<T>>
    {
        public List<T> apply(final T o)
        {
            return ImmutableList.of(o);
        }
    }

    public static <F, T> Function<F, T> memoize(final Function<F, T> delegate, final MapMaker mapMaker)
    {
        return new Function<F, T>()
        {
            final Map<F, T> map = mapMaker.makeComputingMap(delegate);

            public T apply(final F from)
            {
                return map.get(from);
            }
        };
    }

    static final class Memoizer<F, T> implements Function<F, T>
    {
        final Map<F, T> map;

        Memoizer(final Function<F, T> delegate, final MapMaker mapMaker)
        {
            map = mapMaker.makeComputingMap(delegate);
        }

        public T apply(final F from)
        {
            return map.get(from);
        }
    };

    public static Function<String, Either<NumberFormatException, Long>> parseLong()
    {
        return ParseLong.INSTANCE;
    }

    private enum ParseLong implements Function<String, Either<NumberFormatException, Long>>
    {
        INSTANCE;

        public Either<NumberFormatException, Long> apply(final String s)
        {
            try
            {
                return right(Long.valueOf(s));
            }
            catch (final NumberFormatException e)
            {
                return left(e);
            }
        }
    }

    public static <A> Function<A, Iterator<A>> singletonIterator()
    {
        return new Function<A, Iterator<A>>()
        {
            public Iterator<A> apply(final A a)
            {
                return Iterators.singletonIterator(a);
            }
        };
    }

    public static Function<Object, String> toStringFunction()
    {
        return com.google.common.base.Functions.toStringFunction();
    }

    public static Function<String, Either<NumberFormatException, Integer>> parseInt()
    {
        return ParseInt.INSTANCE;
    }

    private enum ParseInt implements Function<String, Either<NumberFormatException, Integer>>
    {
        INSTANCE;

        public Either<NumberFormatException, Integer> apply(final String s)
        {
            try
            {
                return right(Integer.valueOf(s));
            }
            catch (final NumberFormatException e)
            {
                return left(e);
            }
        }
    }

    public static Function<String, Option<String>> trimToNone()
    {
        return TrimToNone.INSTANCE;
    }

    private enum TrimToNone implements Function<String, Option<String>>
    {
        INSTANCE;

        public Option<String> apply(final String s)
        {
            if (s == null)
            {
                return none();
            }
            final String trimmed = s.trim();
            return trimmed.isEmpty() ? Option.<String> none() : some(trimmed);
        }
    }

    public static <T> Function<T, T> identity()
    {
        return com.google.common.base.Functions.identity();
    }

    public static <A> Function<A, Option<A>> option()
    {
        return new ToOption<A>();
    }

    private static class ToOption<A> implements Function<A, Option<A>>
    {
        public Option<A> apply(final A from)
        {
            return Option.option(from);
        }
    }
}

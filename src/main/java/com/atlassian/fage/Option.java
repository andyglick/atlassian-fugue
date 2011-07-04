package com.atlassian.fage;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.fage.Either.Left;
import com.atlassian.fage.Either.Right;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that encapsulates null (missing) values. An Option may be either
 * {@link Some some value} or {@link None not}.
 * <p>
 * If it is a value it may be tested with the {@link #isDefined()} method, but
 * more often it is useful to either return the value or an alternative if
 * {@link #getOrElse(Object) not set}, or {@link #map(Function) map} or
 * {@link #filter(Predicate) filter}.
 * <p>
 * Mapping a None of type A to type B will simply return {@link None} of type B
 * if performed on a {@link None} of type A. Similarly, filtering will always
 * fail on a {@link None}.
 * <p>
 * While this class is public and abstract it does not expose a constructor as
 * only the concrete {@link Some} and {@link None} subclasses are meant to be
 * used.
 * 
 * @param <T> the value type.
 */
public abstract class Option<A> implements Iterable<A>, Supplier<A>
{
    /**
     * Factory method for {@link Option} instances.
     * 
     * @param <A> the held type
     * @param a the value to hold
     * @return a {@link Some} if the parameter is not null or a {@link None} if
     * it is
     */
    public static <A> Option<A> option(final A a)
    {
        return (a == null) ? Option.<A> none() : some(a);
    }

    /**
     * Factory method for {@link Some} instances.
     * 
     * @param <A> the held type
     * @param a the value to hold
     * @return a {@link Some} if the parameter is not null
     * @throws NullPointerException if the parameter is null
     */
    public static <A> Option<A> some(final A value)
    {
        return new Some<A>(value);
    }

    /**
     * Factory method for {@link None} instances.
     * 
     * @param <A> the held type
     * @return a {@link None}
     */
    public static <A> Option<A> none()
    {
        @SuppressWarnings("unchecked")
        final Option<A> result = (Option<A>) NONE;
        return result;
    }

    /**
     * Factory method for {@link None} instances where the type token is handy.
     * Allows calling in-line where the type inferencer would otherwise
     * complain.
     * 
     * @param <A> the held type
     * @param type token of the right type, unused, only here for the type
     * inferencer
     * @return a {@link None}
     */
    public static <A> Option<A> none(final Class<A> type)
    {
        return none();
    }

    /**
     * Find the first option that isDefined, or if there aren't any, then none.
     * 
     * @param <T> the held type
     * @param options an Iterable of options to search through
     */
    public static <T> Option<T> find(final Iterable<Option<T>> options)
    {
        for (final Option<T> option : options)
        {
            if(option.isDefined())
            {
                return option;
            }
        }
        return none();
    }

    /**
     * Filter out undefined options.
     * 
     * @param <T> the held type
     * @param options many options that may or may not be defined
     * @return the filtered options
     */
    public static <T> Iterable<Option<T>> filterNone(final Iterable<Option<T>> options)
    {
        return Iterables.filter(options, defined());
    }

    /**
     * Filters defined options only.
     * 
     * @param <T> the held type
     * @return the filtered options
     */
    public static <T> Predicate<T> defined()
    {
        @SuppressWarnings("unchecked")
        final Predicate<T> result = (Predicate<T>) DEFINED;
        return result;
    }

    /**
     * Supplies {@link None} as required. Useful as the zero value for folds.
     * 
     * @param <A> the held type
     * @return a {@link Supplier} of {@link None} instances
     */
    public static <A> Supplier<Option<A>> noneSupplier()
    {
        return Suppliers.ofInstance(Option.<A> none());
    }

    //
    // ctors
    //

    /** do not constructor */
    Option()
    {}

    //
    // abstract methods
    //

    /**
     * If this is a some value apply the some function, otherwise get the none
     * value.
     * 
     * @param <B> the result type
     * @param none the supplier of the None type
     * @param some the function to apply if we are a some
     * @return the appropriate value
     */
    public abstract <B> B fold(Supplier<? extends B> none, Function<? super A, B> some);

    /**
     * Get the value if defined. Throw an exception otherwise.
     * 
     * @return the wrapped value
     * @throws NoSuchElementException if this is a none
     */
    public abstract A get();

    /**
     * @return {@code true} if this is a {@code some}, {@code false} otherwise.
     */
    public abstract boolean isDefined();

    //
    // methods
    //

    /**
     * Get the value if defined, otherwise returns {@code other}.
     * 
     * @param other value to return if this is a {@code none}
     * @return wrapped value if this is a {@code some}, otherwise returns
     * {@code other}
     */
    public final <B extends A> A getOrElse(final B other)
    {
        return getOrElse(Suppliers.<A> ofInstance(other));
    }

    /**
     * Get the value if defined or call the supplier and return its value if
     * not.
     * 
     * @return the wrapped value or the value from the {@code Supplier}
     */
    public final A getOrElse(final Supplier<A> supplier)
    {
        return fold(supplier, Functions.<A> identity());
    }

    /**
     * Get the value if defined or null if not.
     * <p>
     * Although the use of null is discouraged, code written to use Option must
     * often interface with code that expects and returns nulls.
     */
    public final A getOrNull()
    {
        return fold(Suppliers.<A> alwaysNull(), Functions.<A> identity());
    }

    /**
     * Apply {@code f} to the value if defined.
     * <p>
     * Transforms to an option of the functions result type.
     * 
     * @param <B> return type of {@code f}
     * @param f function to apply to wrapped value
     * @return new wrapped value
     */
    public final <B> Option<B> map(final Function<? super A, B> f)
    {
        return flatMap(compose(Functions.<B> option(), f));
    }

    /**
     * Apply {@code f} to the value if defined.
     * <p>
     * Transforms to an option of the functions result type.
     * 
     * @param <B> return type of {@code f}
     * @param f function to apply to wrapped value
     * @return value returned from {@code f}
     */
    public final <B> Option<B> flatMap(final Function<? super A, Option<B>> f)
    {
        checkNotNull(f);
        return fold(Option.<B> noneSupplier(), f);
    }

    /**
     * Returns this {@link Option} if it is nonempty <strong>and</strong>
     * applying the predicate to this option's value returns true. Otherwise,
     * return {@link #none()}.
     * 
     * @param p the predicate to test
     */
    public final Option<A> filter(final Predicate<? super A> p)
    {
        checkNotNull(p);
        return (isEmpty() || p.apply(get())) ? this : Option.<A> none();
    }

    /**
     * Returns this {@link Option} if it is nonempty <strong>and</strong>
     * applying the predicate to this option's value returns true. Otherwise,
     * return {@link #none()}.
     * 
     * @param p the predicate to test
     */
    public final boolean exists(final Predicate<A> p)
    {
        checkNotNull(p);
        return isDefined() && p.apply(get());
    }

    /**
     * @return {@code false} if this is a {@code some}, {@code true} otherwise.
     */
    public final boolean isEmpty()
    {
        return !isDefined();
    }

    /**
     * Returns a {@link Left} containing the given supplier's value if this is
     * empty, or a {@link Right} containing this option's value if this option
     * is defined.
     * 
     * @param left the Supplier to evaluate and return if this is empty
     * @see toLeft
     */
    public final <X> Either<X, A> toRight(final Supplier<X> left)
    {
        return isEmpty() ? Either.<X, A> left(left.get()) : Either.<X, A> right(get());
    }

    /**
     * Returns a {@link Left} containing the given supplier's value if this is
     * empty, or a {@link Right} containing this option's value if this option
     * is defined.
     * 
     * @param left the Supplier to evaluate and return if this is empty
     * @see toLeft
     */
    public final <X> Either<A, X> toLeft(final Supplier<X> right)
    {
        return isEmpty() ? Either.<A, X> right(right.get()) : Either.<A, X> left(get());
    }

    /**
     * @return a single element iterator if this is a {@code some}, an empty one
     * otherwise.
     */
    public final Iterator<A> iterator()
    {
        return fold(Suppliers.ofInstance(Iterators.<A> emptyIterator()), Functions.<A> singletonIterator());
    }

    @Override
    public final int hashCode()
    {
        return fold(NONE_HASH, SomeHashCode.instance());
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if ((obj == null) || !(obj instanceof Option<?>))
        {
            return false;
        }
        final Option<?> other = (Option<?>) obj;
        return other.fold(isDefined() ? Suppliers.alwaysFalse() : Suppliers.alwaysTrue(), valuesEqual());
    }

    @Override
    public final String toString()
    {
        return fold(NONE_STRING, SomeString.instance());
    }

    //
    // util methods
    //

    private Function<Object, Boolean> valuesEqual()
    {
        return new Function<Object, Boolean>()
        {
            public Boolean apply(final Object obj)
            {
                return get().equals(obj);
            }
        };
    }

    //
    // static members
    //

    private static final Option<Object> NONE = new Option<Object>()
    {
        @Override
        public <B> B fold(final Supplier<? extends B> none, final Function<? super Object, B> some)
        {
            return none.get();
        }

        @Override
        public Object get()
        {
            throw new NoSuchElementException();
        }

        @Override
        public boolean isDefined()
        {
            return false;
        }
    };

    private static final Supplier<String> NONE_STRING = Suppliers.ofInstance("none()");
    private static final Supplier<Integer> NONE_HASH = Suppliers.ofInstance(31);

    private static final Predicate<Option<?>> DEFINED = new Predicate<Option<?>>()
    {
        @Override
        public boolean apply(final Option<?> option)
        {
            return option.isDefined();
        }
    };

    //
    // inner classes
    //

    /**
     * The big one, the actual implementation class.
     */
    private static final class Some<A> extends Option<A>
    {
        private final A value;

        private Some(final A value)
        {
            this.value = checkNotNull(value, "value");
        }

        @Override
        public <B> B fold(final Supplier<? extends B> none, final Function<? super A, B> f)
        {
            return f.apply(value);
        }

        @Override
        public A get()
        {
            return value;
        }

        @Override
        public boolean isDefined()
        {
            return true;
        }
    }

    private enum SomeString implements Function<Object, String>
    {
        INSTANCE;

        public String apply(final Object obj)
        {
            return String.format("some(%s)", obj);
        }

        @SuppressWarnings("unchecked")
        static <A> Function<A, String> instance()
        {
            // Some IDEs reckon this doesn't compile. They are wrong. It compiles and is correct.
            return (Function<A, String>) SomeString.INSTANCE;
        }
    }

    private enum SomeHashCode implements Function<Object, Integer>
    {
        INSTANCE;

        public Integer apply(final Object a)
        {
            return a.hashCode();
        }

        @SuppressWarnings("unchecked")
        static <A> Function<A, Integer> instance()
        {
            // Some IDEs reckon this doesn't compile. They are wrong. It compiles and is correct.
            return (Function<A, Integer>) SomeHashCode.INSTANCE;
        }
    }
}

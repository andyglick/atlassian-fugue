package com.atlassian.fage;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.singletonIterator;

import com.atlassian.util.concurrent.NotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that encapsulates null (missing) values. An Option may be either {@link Some some value} or {@link None
 * not}.
 * <p/>
 * If it is a value it may be tested with the {@link #isSet()} method, but more often it is useful to either return the
 * value or an alternative if {@link #getOrElse(Object) not set}, or {@link #map(Function) map} or {@link
 * #filter(Predicate) filter}.
 * <p/>
 * Mapping a None of type A to type B will simply return {@link None} of type B if performed on a {@link None} of type
 * A. Similarly, filtering will always fail on a {@link None}.
 * <p/>
 * While this class is public and abstract it does not expose a constructor as only the concrete {@link Some} and {@link
 * None} subclasses are meant to be used.
 *
 * @param <T> the value type.
 */
public abstract class Option<T> implements Iterable<T>
{

    //
    // factory methods
    //

    private static final None<?> NONE = new None<Object>();
    private static final Predicate<Option<?>> SET = new Predicate<Option<?>>()
    {
        @Override
        public boolean apply(final Option<?> option)
        {
            return option.isSet();
        }
    };

    /**
     * Get a none of the required type.
     * @param <T> the type.
     * @return a none.
     */
    public static <T> None<T> none()
    {
        @SuppressWarnings( { "unchecked" })
        final None<T> result = (None<T>) NONE;
        return result;
    }

    /**
     * Get some option for the value if not null, otherwise none.
     * 
     * @param <T> the option type
     * @param value if not null will return {@link Some some option}, if null will return {@link None}.
     * @return an option
     */
    public static <T> Option<T> get(final T value)
    {
        if (value == null)
        {
            return none();
        }
        return new Some<T>(value);
    }

    //
    // utilities
    //

    /**
     * Find the first option that isSet, or if there aren't any, then none.
     */
    public static <T> Option<T> find(final Iterable<Option<T>> options)
    {
        return Iterables.find(options, SET, Option.<T> none());
    }

    /**
     * Filter out unset options.
     * 
     * @param <T> the type
     * @param options many options that may or may not be set
     * @return the filtered options 
     */
    public static <T> Iterable<Option<T>> filterNone(final Iterable<Option<T>> options)
    {
        return Iterables.filter(options, set());
    }

    public static <T> Predicate<T> set()
    {
        @SuppressWarnings("unchecked")
        final Predicate<T> result = (Predicate<T>) SET;
        return result;
    }

    //
    // constructors
    //

    Option()
    {}

    //
    // methods
    //

    /**
     * Get the value of this Option, requires that the value is set.
     *
     * @throws NoSuchElementException if the option is empty
     */
    public abstract T get() throws NoSuchElementException;

    /**
     * Is this option not empty?
     */
    public abstract boolean isSet();

    /**
     * Get the value if set, or the parameter if not.
     */
    public abstract T getOrElse(final T value);

    /**
     * Apply the function to the value if set and return an {@link Option} of the function value. Return {@link None} if
     * not set or the function returns null.
     *
     * @param <V> the return type
     * @param function the function that takes the value and produces the result
     * @throws NullPointerException if function is null
     */
    public abstract <V> Option<V> map(@NotNull Function<T, V> function);

    /**
     * If the option is empty, or it is nonempty and the given predicate yields false on its value, return None. Otherwise
     * return the option value itself.
     * 
     * @throws NullPointerException if predicate is null
     */
    public abstract Option<T> filter(@NotNull Predicate<T> predicate);

    //
    // inner class implementations
    //

    public static final class Some<T> extends Option<T>
    {
        private final T value;

        Some(final T value)
        {
            this.value = checkNotNull(value);
        }

        @Override
        public boolean isSet()
        {
            return true;
        }

        @Override
        public T get()
        {
            return value;
        }

        @Override
        public T getOrElse(final T value)
        {
            return this.value;
        }

        @Override
        public <V> Option<V> map(final Function<T, V> function)
        {
            return get(checkNotNull(function).apply(value));
        }

        @Override
        public Option<T> filter(final Predicate<T> predicate)
        {
            if (predicate.apply(value))
            {
                return this;
            }
            return none();
        }

        public Iterator<T> iterator()
        {
            return singletonIterator(value);
        }
    }

    public static final class None<T> extends Option<T>
    {
        @Override
        public T get()
        {
            throw new NoSuchElementException();
        }

        @Override
        public T getOrElse(final T value)
        {
            return value;
        }

        @Override
        public boolean isSet()
        {
            return false;
        }

        @Override
        public <V> Option<V> map(final Function<T, V> function)
        {
            checkNotNull(function);
            return none();
        }

        @Override
        public Option<T> filter(final Predicate<T> predicate)
        {
            checkNotNull(predicate);
            return this;
        }

        public Iterator<T> iterator()
        {
            return Iterators.emptyIterator();
        }
    }
}
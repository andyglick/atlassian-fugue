package com.atlassian.fage;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.singletonIterator;

import com.atlassian.util.concurrent.NotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

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

    static <T> None<T> none()
    {
        return (None<T>) NONE;
    }

    public static <T> Option<T> get(final T value)
    {
        if (value == null)
        {
            return none();
        }
        return new Some<T>(value);
    }

    //
    // constructors
    //

    Option() {}

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
            return EmptyIterator.instance();
        }
    }

    static final class EmptyIterator<T> implements Iterator<T>
    {
        static <T> Iterator<T> instance()
        {
            return (Iterator<T>) INSTANCE;
        }

        private static final Iterator<?> INSTANCE = new EmptyIterator<Object>();

        public boolean hasNext()
        {
            return false;
        }

        public T next()
        {
            throw new NoSuchElementException();
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
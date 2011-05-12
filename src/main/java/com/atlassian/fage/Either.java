package com.atlassian.fage;

import static com.atlassian.fage.Option.option;
import static com.atlassian.fage.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

/**
 * A class that acts as a container for a value of one of two types. An Either will be either {@link Left} or {@link
 * Right}.
 * <p/>
 * Checking which type an Either is can be done be calling the @{@link #isLeft()} and {@link #isRight()} methods.
 * <p/>
 * Eithers can be used to express a success or failure case. By convention, Right is used to store the success value,
 * (you can use the play on words "right" == "correct" as a mnemonic) and Left is used to store failure values (such
 * as exceptions).
 * <p/>
 * While this class is public and abstract it does not expose a constructor as only the concrete {@link Left} and {@link
 * Right} subclasses are meant to be used.
 * <p/>
 * Eithers are immutable, but do not force immutability on contained objects; if the contained objects are mutable then
 * equals and hashcode methods should not be relied on.
 */
public abstract class Either<L, R>
{
    //
    // factory methods
    //

    /**
     * @param left the value to be stored, must not be null
     * @return a Left containing the supplied value
     */
    public static <L, R> Either<L, R> left(final L left)
    {
        checkNotNull(left);
        return new Left<L, R>(left);
    }

    /**
     * @param right the value to be stored, must not be null
     * @return a Right containing the supplied value
     */
    public static <L, R> Either<L, R> right(final R right)
    {
        checkNotNull(right);
        return new Right<L, R>(right);
    }

    //
    // static utility methods
    //

    /**
     * Extracts an object from an Either, regardless of the side in which it is stored, provided both sides contain the
     * same type. This method will never return null.
     */
    public static <T> T merge(final Either<T, T> either)
    {
        if (either.isLeft())
        {
            return either.left().get();
        }
        return either.right().get();
    }

    /**
     * Creates an Either based on a boolean expression. If predicate is true, a Right wil be returned containing the
     * supplied right value; if it is false, a Left will be returned containing the supplied left value. 
     */
    public static <L, R> Either<L, R> cond(final boolean predicate, final R right, final L left)
    {
        return (predicate) ? Either.<L, R> right(right) : Either.<L, R> left(left);
    }

    //
    // constructors
    //

    Either()
    {}

    //
    // methods
    //

    public boolean isLeft()
    {
        return false;
    }

    public boolean isRight()
    {
        return false;
    }

    /**
     * @return an option wrapping the left value of this either
     */
    public Option<L> left()
    {
        return Option.none();
    }

    /**
     * @return an option wrapping the right value of this either
     */
    public Option<R> right()
    {
        return Option.none();
    }

    /**
     * Applies function to the held value if it is a Left.
     * 
     * @param <V> the return type
     * @param function takes the value and produces the result
     * @return the result of the function application
     * @throws UnsupportedOperationException if this Either is not a Left
     */
    public <V> V mapLeft(final Function<L, V> function)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Applies function to the held value if it is a Right.
     * 
     * @param <V> the return type
     * @param function takes the value and produces the result
     * @return the result of the function application
     * @throws UnsupportedOperationException is this Either is not a Right
     */
    public <V> V mapRight(final Function<R, V> function)
    {
        throw new UnsupportedOperationException();
    }

    public abstract Either<R, L> swap();

    public abstract <V> V fold(Function<L, V> ifLeft, Function<R, V> ifRight);

    //
    // inner class implementations
    //

    public static final class Left<L, R> extends Either<L, R>
    {
        private final L value;

        public Left(final L value)
        {
            checkNotNull(value);
            this.value = value;
        }

        @Override
        public Option<L> left()
        {
            return option(value);
        }

        @Override
        public boolean isLeft()
        {
            return true;
        }

        @Override
        public Either<R, L> swap()
        {
            return right(value);
        }

        @Override
        public <V> V fold(final Function<L, V> ifLeft, final Function<R, V> ifRight)
        {
            return mapLeft(ifLeft);
        }

        @Override
        public <V> V mapLeft(final Function<L, V> function)
        {
            return function.apply(value);
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if ((o == null) || (getClass() != o.getClass()))
            {
                return false;
            }

            return value.equals(((Left<?, ?>) o).value);
        }

        @Override
        public int hashCode()
        {
            return value.hashCode();
        }

        @Override
        public String toString()
        {
            return "Either:Left { " + value.toString() + " } ";
        }
    }

    public static final class Right<L, R> extends Either<L, R>
    {
        private final R value;

        public Right(final R value)
        {
            checkNotNull(value);
            this.value = value;
        }

        @Override
        public Option<R> right()
        {
            return some(value);
        }

        @Override
        public boolean isRight()
        {
            return true;
        }

        @Override
        public Either<R, L> swap()
        {
            return left(value);
        }

        @Override
        public <V> V fold(final Function<L, V> ifLeft, final Function<R, V> ifRight)
        {
            return mapRight(ifRight);
        }

        @Override
        public <V> V mapRight(final Function<R, V> function)
        {
            return function.apply(value);
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if ((o == null) || (getClass() != o.getClass()))
            {
                return false;
            }

            return value.equals(((Right<?, ?>) o).value);
        }

        @Override
        public int hashCode()
        {
            return value.hashCode();
        }

        @Override
        public String toString()
        {
            return "Either:Right { " + value.toString() + " } ";
        }
    }
}

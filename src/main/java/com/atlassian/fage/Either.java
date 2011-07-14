package com.atlassian.fage;

import static com.atlassian.fage.Option.option;
import static com.atlassian.fage.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

/**
 * A class that acts as a container for a value of one of two types. An Either will be either {@link Either.Left Left}
 * or {@link Either.Right Right}.
 * <p/>
 * Checking which type an Either is can be done by calling the @{@link #isLeft()} and {@link #isRight()} methods.
 * <p/>
 * Eithers can be used to express a success or failure case. By convention, Right is used to store the success value,
 * (you can use the play on words "right" == "correct" as a mnemonic) and Left is used to store failure values (such
 * as exceptions).
 * <p/>
 * While this class is public and abstract it does not expose a constructor as only the concrete Left and Right
 * subclasses are meant to be used.
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
     * @return an Option wrapping the left value of this either
     */
    public Option<L> left()
    {
        return Option.none();
    }

    /**
     * @return an Option wrapping the right value of this either
     */
    public Option<R> right()
    {
        return Option.none();
    }

    /**
     * @return an Either that is a Left if this is a Right or a Right if this is a Left. The value remains the same.
     */
    public abstract Either<R, L> swap();

    /**
     * Applies the function to the wrapped value, applying ifLeft it this is a Left and ifRight if this is a Right.
     * 
     * @param ifLeft the function to apply if this is a Left
     * @param ifRight the function to apply if this is a Right
     * @return the result of the applies function
     */
    public abstract <V> V fold(Function<? super L, V> ifLeft, Function<? super R, V> ifRight);

    //
    // inner class implementations
    //

    static final class Left<L, R> extends Either<L, R>
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
        public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight)
        {
            return left().map(ifLeft).get();
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if ((o == null) || !(o instanceof Left<?, ?>))
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
            return "Either.Left(" + value.toString() + ")";
        }
    }

    static final class Right<L, R> extends Either<L, R>
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
        public <V> V fold(final Function<? super L, V> ifLeft, final Function<? super R, V> ifRight)
        {
            return right().map(ifRight).get();
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if ((o == null) || !(o instanceof Right<?, ?>))
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
            return "Either.Right(" + value.toString() + ")";
        }
    }
}

package com.atlassian.fugue;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Maybe<A> extends Supplier<A>, Iterable<A>
{
    /**
     * Get the value if defined. Throw an exception otherwise.
     * 
     * @return the wrapped value
     * @throws NoSuchElementException if this is a none
     */
    A get();

    /**
     * Get the value if defined, otherwise returns {@code other}.
     * 
     * @param other value to return if this is a {@code none}
     * @return wrapped value if this is a {@link Option.Some Some}, otherwise returns
     * {@code other}
     */
    <B extends A> A getOrElse(final B other);

    /**
     * Get the value if defined or call the supplier and return its value if
     * not.
     * 
     * @return the wrapped value or the value from the {@code Supplier}
     */
    A getOrElse(final Supplier<A> supplier);

    /**
     * Get the value if defined or null if not.
     * <p>
     * Although the use of null is discouraged, code written to use Option must
     * often interface with code that expects and returns nulls.
     */
    A getOrNull();

    /**
     * Get the value or throws an error with the supplied message if called.
     * <p>
     * Used when absolutely sure this {@link #isDefined()}.
     * 
     * @param msg the message for the error.
     * @return the contained value.
     */
    A getOrError(Supplier<String> msg);

    /**
     * @return {@code true} if this is a {@link Option.Some Some}, {@code false} otherwise.
     */
    boolean isDefined();

    /**
     * @return {@code false} if this is a {@link Option.Some Some}, {@code true} otherwise.
     */
    boolean isEmpty();

    /**
     * Returns this {@link Option} if it is nonempty <strong>and</strong>
     * applying the predicate to this option's value returns true. Otherwise,
     * return {@link #none()}.
     * 
     * @param p the predicate to test
     */
    boolean exists(final Predicate<A> p);

    /**
     * @return a single element iterator if this is a {@link Option.Some Some}, an empty one
     * otherwise.
     */
    Iterator<A> iterator();

    /**
     * Perform the given side-effect for each contained element.
     */
    void foreach(Effect<A> effect);

    /**
     * Returns <code>true</code> if no value or returns the result of the application of the given
     * function to the value.
     *
     * @param p The predicate function to test on the contained value.
     * @return <code>true</code> if no value or returns the result of the application of the given
     *         function to the value.
     */
    boolean forall(final Predicate<A> p);

    //
    // stuff that can't be made put on an interface without HKT
    //

    /**
     * Apply {@code f} to the value if defined.
     * <p>
     * Transforms to an option of the functions result type.
     * 
     * @param <B> return type of {@code f}
     * @param f function to apply to wrapped value
     * @return new wrapped value
     */
    //<B> Option<B> map(final Function<? super A, B> f);

    /**
     * Apply {@code f} to the value if defined.
     * <p>
     * Transforms to an option of the functions result type.
     * 
     * @param <B> return type of {@code f}
     * @param f function to apply to wrapped value
     * @return value returned from {@code f}
     */
    //<B> Option<B> flatMap(final Function<? super A, Option<B>> f);

    /**
     * Returns this {@link Option} if it is nonempty <strong>and</strong>
     * applying the predicate to this option's value returns true. Otherwise,
     * return {@link #none()}.
     * 
     * @param p the predicate to test
     */
    //Option<A> filter(final Predicate<? super A> p);
}
package com.atlassian.fugue;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import java.util.Collections;

import static com.atlassian.jira.plugin.devstatus.util.ValidationAlgebra.*;

/**
 * Utility functions to support the Validation Applicative
 */
public final class Validations {
    // If we support an Applicative interface, this is an alias for point().
    public static <EE, A> Validation<EE, A> succeed (A a){
        return new Success<>(a);
    }

    public static <EE, A> Validation<EE, A> fail (EE e){
        return new Failure<>(e);
    }

    public static <EE,A> Validation<EE,A> condition(boolean cond, EE e, A a) {
        return cond ? succeed(a) : fail(e);
    }

    public static <EE,A> Validation<EE,A> condition(Predicate<A> cond, EE e, A a) {
        return cond.apply(a) ? succeed(a) : fail(e);
    }

    // Provided only to provide an obvious base case for the structural recursion of apply2/3/4...
    @Nonnull
    public static <EE, A, R> Validation<EE, R> apply(
            @Nonnull Validation<EE, A> fa,
            @Nonnull Function<A,R> f) {
        return fa.map(f);
    }

    @Nonnull
    public static <EE, A, B, R> Validation<EE, R> apply(
            @Nonnull SemiGroup<EE> s,
            @Nonnull Validation<EE, A> fa,
            @Nonnull Validation<EE, B> fb,
            @Nonnull Function<A,Function<B,R>> f) {

        return fa.match(
                failure -> failure.accumulate(s, fb),
                success -> apply(fb, f.apply(success.getValue())));
    }

    @Nonnull
    public static <EE, A, B, C, R> Validation<EE, R> apply(
            @Nonnull SemiGroup<EE> s,
            @Nonnull Validation<EE, A> fa,
            @Nonnull Validation<EE, B> fb,
            @Nonnull Validation<EE, C> fc,
            @Nonnull Function<A,Function<B,Function<C,R>>> f) {

        return fa.match(
                failure -> failure.accumulate(s, fb, fc),
                success -> apply(s, fb, fc, f.apply(success.getValue())));
    }

    @Nonnull
    public static <EE, A, B, C, D, R> Validation<EE, R> apply(
            @Nonnull SemiGroup<EE> s,
            @Nonnull Validation<EE, A> fa,
            @Nonnull Validation<EE, B> fb,
            @Nonnull Validation<EE, C> fc,
            @Nonnull Validation<EE, D> fd,
            @Nonnull Function<A,Function<B,Function<C,Function<D,R>>>> f) {

        return fa.match(
                failure -> failure.accumulate(s, fb, fc, fd),
                success -> apply(s, fb, fc, fd, f.apply(success.getValue())));
    }

    @Nonnull
    public static <EE, A, B, C, D, E, R> Validation<EE, R> apply(
            @Nonnull SemiGroup<EE> s,
            @Nonnull Validation<EE, A> fa,
            @Nonnull Validation<EE, B> fb,
            @Nonnull Validation<EE, C> fc,
            @Nonnull Validation<EE, D> fd,
            @Nonnull Validation<EE, E> fe,
            @Nonnull Function<A,Function<B,Function<C,Function<D,Function<E,R>>>>> f) {

        return fa.match(
                failure -> failure.accumulate(s, fb, fc, fd, fe),
                success -> apply(s, fb, fc, fd, fe, f.apply(success.getValue())));
    }

    @Nonnull
    public static <EE, A, B, C, D, E, F, R> Validation<EE, R> apply(
            @Nonnull SemiGroup<EE> s,
            @Nonnull Validation<EE, A> fa,
            @Nonnull Validation<EE, B> fb,
            @Nonnull Validation<EE, C> fc,
            @Nonnull Validation<EE, D> fd,
            @Nonnull Validation<EE, E> fe,
            @Nonnull Validation<EE, F> ff,
            @Nonnull Function<A,Function<B,Function<C,Function<D,Function<E,Function<F,R>>>>>> f) {

        return fa.match(
                failure -> failure.accumulate(s, fb, fc, fd, fe, ff),
                success -> apply(s, fb, fc, fd, fe, ff, f.apply(success.getValue())));
    }

    @Nonnull
    public static <EE, A, B, C, D, E, F, G, R> Validation<EE, R> apply(
            @Nonnull SemiGroup<EE> s,
            @Nonnull Validation<EE, A> fa,
            @Nonnull Validation<EE, B> fb,
            @Nonnull Validation<EE, C> fc,
            @Nonnull Validation<EE, D> fd,
            @Nonnull Validation<EE, E> fe,
            @Nonnull Validation<EE, F> ff,
            @Nonnull Validation<EE, G> fg,
            @Nonnull Function<A,Function<B,Function<C,Function<D,Function<E,Function<F,Function<G,R>>>>>>> f) {

        return fa.match(
                failure -> failure.accumulate(s, fb, fc, fd, fe, ff, fg),
                success -> apply(s, fb, fc, fd, fe, ff, fg, f.apply(success.getValue())));
    }
    public interface SemiGroup<A> {
        A add(A a1, A a2);
    }

    public interface SemiGroupApp<A,B> extends SemiGroup<A> {
        A point(B b);
    }

    public static <A> SemiGroupApp<Iterable<A>,A> iterSG (Class<A> c) {
        return new SemiGroupApp<Iterable<A>, A>() {
            @Override
            public Iterable<A> point(A a) {
                return Collections.singleton(a);
            }

            @Override
            public Iterable<A> add(Iterable<A> a1, Iterable<A> a2) {
                return Iterables.concat(a1, a2);
            }
        };
    }
}

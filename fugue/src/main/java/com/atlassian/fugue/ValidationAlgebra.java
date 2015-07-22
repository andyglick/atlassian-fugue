package com.atlassian.fugue;

import com.google.common.base.Function;

import javax.annotation.Nonnull;

import static com.atlassian.jira.plugin.devstatus.util.Validations.*;

/**
 * Created by amuys on 17/07/2015.
 */
class ValidationAlgebra {
    static final class Success<EE, A> extends Validation<EE,A> {
        private A a;

        Success(A a) {
            this.a = a;
        }

        @Nonnull
        @Override
        public <B> B fold(@Nonnull Function<EE, B> onError, @Nonnull Function<A, B> onSuccess) {
            return onSuccess.apply(a);
        }

        @Nonnull
        @Override
        <B> B match(@Nonnull Function<Failure<EE, A>, B> onError, @Nonnull Function<Success<EE, A>, B> onSuccess) {
            return onSuccess.apply(this);
        }

        @Nonnull
        @Override
        public <B> Validation<EE, B> map(@Nonnull Function<A, B> f) {
            return succeed(f.apply(a));
        }

        <B> Success<EE, B> mp(Function<A, B> f) {
            return new Success<>(f.apply(a));
        }

        @Nonnull
        @Override
        public <B> Validation<EE, B> ap(@Nonnull Validations.SemiGroup<EE> s, @Nonnull Validation<EE, Function<A, B>> ff) {
            return ff.fold(ee -> fail(ee), f -> succeed(f.apply(a)));
        }

        @Nonnull
        A getValue() {
            return a;
        }
    }


    static final class Failure<EE,A> extends Validation<EE,A> {
        final EE e;

        Failure(EE e) {
            this.e = e;
        }

        static <EE, A> Failure<EE, A> failure(EE ee) {
            return new Failure<>(ee);
        }

        <R> Failure<EE, R> failure() {
            return failure(e);
        }

        @Nonnull
        @Override
        public <B> B fold(@Nonnull Function<EE, B> onError, @Nonnull Function<A, B> onSuccess) {
            return onError.apply(e);
        }

        /**
         * This is an encoding in java of an ML/Scala/Haskell style pattern match over the Validation ADT.
         * This is for internal use only: Users of the Validation API should be calling fold instead.
         *
         * @param onError The Failure case
         * @param onSuccess The Success case
         */
        @Nonnull
        @Override
        <B> B match(@Nonnull Function<Failure<EE, A>, B> onError, @Nonnull Function<Success<EE, A>, B> onSuccess) {
            return onError.apply(this);
        }

        @Nonnull
        @Override
        public <B> Validation<EE, B> map(@Nonnull Function<A, B> f) {
            return fail(e);
        }

        @Nonnull
        @Override
        public <B> Validation<EE, B> ap(@Nonnull SemiGroup<EE> s, @Nonnull Validation<EE, Function<A, B>> ff) {
            return ff.fold(ee -> fail(s.add(e, ee)), f -> fail(e));
        }

        EE getError() {
            return this.e;
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                Validation<EE, ?> vb) {
            return accLoop(s, v -> v.failure(), vb);
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                Validation<EE, ?> vb,
                Validation<EE, ?> vc) {
            return accLoop(s, v -> v.accumulate(s, vc), vb);
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                                         Validation<EE, ?> vb,
                                         Validation<EE, ?> vc,
                                         Validation<EE, ?> vd) {
            return accLoop(s, v -> v.accumulate(s, vc, vd), vb);
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                                         Validation<EE, ?> vb,
                                         Validation<EE, ?> vc,
                                         Validation<EE, ?> vd,
                                         Validation<EE, ?> ve) {
            return accLoop(s, v -> v.accumulate(s, vc, vd, ve), vb);
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                                         Validation<EE, ?> vb,
                                         Validation<EE, ?> vc,
                                         Validation<EE, ?> vd,
                                         Validation<EE, ?> ve,
                                         Validation<EE, ?> vf) {
            return accLoop(s, v -> v.accumulate(s, vc, vd, ve, vf), vb);
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                                         Validation<EE, ?> vb,
                                         Validation<EE, ?> vc,
                                         Validation<EE, ?> vd,
                                         Validation<EE, ?> ve,
                                         Validation<EE, ?> vf,
                                         Validation<EE, ?> vg) {
            return accLoop(s, v -> v.accumulate(s, vc, vd, ve, vf, vg), vb);
        }

        <R> Validation<EE, R> accumulate(SemiGroup<EE> s,
                                         Validation<EE, ?> vb,
                                         Validation<EE, ?> vc,
                                         Validation<EE, ?> vd,
                                         Validation<EE, ?> ve,
                                         Validation<EE, ?> vf,
                                         Validation<EE, ?> vg,
                                         Validation<EE, ?> vh) {
            return accLoop(s, v -> v.accumulate(s, vc, vd, ve, vf, vg, vh), vb);
        }

        <R> Validation<EE, R> accLoop(SemiGroup<EE> s, Function<Failure<EE, ?>, Validation<EE, R>> acc, Validation<EE, ?> v) {
            return v.fold(ee -> acc.apply(failure(s.add(e, ee))), x -> acc.apply(this));
        }
    }
}

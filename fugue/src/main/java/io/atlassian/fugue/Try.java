package io.atlassian.fugue;

import java.util.function.Function;

public abstract class Try<T> {

    public interface CheckedFunction<T, R> {
        R apply(T t) throws Throwable;
    }

    public interface CheckedSupplier<T> {

        T get() throws Throwable;

    }

    public static <U> Try<U> of(CheckedSupplier<U> f) {
        try {
            return successful(f.get());
        } catch (Throwable e) {
            return failure(e);
        }
    }

    public static <U> Try<U> failure(Throwable e) {
        return new Failure<>(e);
    }

    public static <U> Try<U> successful(U x) {
        return new Success<>(x);
    }

    public abstract <U> Try<U> map(CheckedFunction<? super T, ? extends U> f);

    public abstract <U> Try<U> flatMap(CheckedFunction<? super T, Try<U>> f);

    public abstract T recover(Function<? super Throwable, T> f);

    public abstract Try<T> recoverWith(CheckedFunction<? super Throwable, Try<T>> f);

    public abstract <U> U handle(Function<? super Throwable, U> failureF, Function<T, U> successF);

    //filter

    private static final class Failure<T> extends Try<T> {

        private final Throwable e;

        public Failure(final Throwable e) {
            this.e = e;
        }

        @Override
        public <U> Try<U> map(final CheckedFunction<? super T, ? extends U> f) {
            return Try.failure(e);
        }

        @Override
        public <U> Try<U> flatMap(final CheckedFunction<? super T, Try<U>> f) {
            return Try.failure(e);
        }

        @Override
        public T recover(final Function<? super Throwable, T> f) {
            return f.apply(e);
        }

        @Override
        public Try<T> recoverWith(final CheckedFunction<? super Throwable, Try<T>> f) {
            try {
                return f.apply(e);
            } catch (Throwable throwable) {
                return Try.failure(e);
            }
        }

        @Override
        public <U> U handle(final Function<? super Throwable, U> failureF, final Function<T, U> successF) {
            return failureF.apply(e);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Failure) {
                return ((Failure) obj).e.equals(this.e);
            } else return false;
        }
    }

    private static final class Success<T> extends Try<T> {

        private final T value;

        public Success(final T value) {
            this.value = value;
        }

        @Override
        public <U> Try<U> map(final CheckedFunction<? super T, ? extends U> f) {
            return Try.of(() -> f.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(final CheckedFunction<? super T, Try<U>> f) {
            try {
                return f.apply(value);
            } catch (Throwable e) {
                return Try.failure(e);
            }
        }

        @Override
        public T recover(final Function<? super Throwable, T> f) {
            return value;
        }

        @Override
        public Try<T> recoverWith(final CheckedFunction<? super Throwable, Try<T>> f) {
            return Try.successful(value);
        }

        @Override
        public <U> U handle(final Function<? super Throwable, U> failureF, final Function<T, U> successF) {
            return successF.apply(value);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Success) {
                return ((Success) obj).value.equals(this.value);
            } else return false;
        }
    }
}

package io.atlassian.fugue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryFailureTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private class TestException extends Exception {
        TestException(final String message) {
            super(message);
        }
    }

    private static final String MESSAGE = "known exception message";
    private final Try<Integer> t = Try.of(() -> {
        throw new TestException(MESSAGE);
    });

    @Test
    public void map() throws Exception {
        assertThat(t.map(x -> true), is(t));
    }

    @Test
    public void isFailure() throws Exception {
        assertThat(t.isFailure(), is(true));
    }

    @Test
    public void isSuccess() throws Exception {
        assertThat(t.isSuccess(), is(false));
    }

    @Test
    public void flatMap() throws Exception {
        assertThat(t.map(x -> true), is(t));
    }

    @Test
    public void recover() throws Exception {
        assertThat(t.recover(x -> 0), is(Try.of(() -> 0)));
    }

    @Test
    public void recoverWith() throws Exception {
        assertThat(t.recoverWith(x -> Try.of(() -> 0)), is(Try.of(() -> 0)));
    }

    @Test
    public void recoverWithCatchesException() throws Exception {
        Try<Integer> recovered = t.recoverWith(x -> {throw new RuntimeException("Failed recovery");});

        assertThat(recovered.isFailure(), is(true));
        assertThat(recovered.getExceptionUnsafe(), instanceOf(RuntimeException.class));
        assertThat(recovered.getExceptionUnsafe().getMessage(), is("Failed recovery"));
    }

    @Test
    public void getOrElse() throws Exception {
        assertThat(t.getOrElse(() -> 0), is(0));
    }

    @Test
    public void filter() throws Exception {
        assertThat(t.filter(x -> true), is(t));
    }

    @Test
    public void fold() throws Exception {
        Exception e = t.fold(Function.identity(), v -> {
            throw new RuntimeException();
        });

        assertThat(e, instanceOf(TestException.class));
        assertThat(e.getMessage(), is(MESSAGE));
    }

    @Test
    public void getUnsafe() throws Exception {
        thrown.expect(NoSuchElementException.class);
        t.getUnsafe();
    }

    @Test
    public void getExceptionUnsafe() throws Exception {
        Exception e = t.getExceptionUnsafe();

        assertThat(e, instanceOf(TestException.class));
        assertThat(e.getMessage(), is(MESSAGE));
    }

    @Test
    public void toEither() throws Exception {
        final Either<Exception, Integer> e = t.toEither();

        assertThat(e.isLeft(), is(true));
        assertThat(e.left().get(), instanceOf(TestException.class));
        assertThat(e.left().get().getMessage(), is(MESSAGE));
    }

    @Test
    public void toOption() throws Exception {
        assertThat(t.toOption(), is(Option.none()));
    }

}
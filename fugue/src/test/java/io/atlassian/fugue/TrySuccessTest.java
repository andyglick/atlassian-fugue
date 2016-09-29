package io.atlassian.fugue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TrySuccessTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final Integer STARTING_VALUE = 0;
    private final Try<Integer> t = Try.of(() -> STARTING_VALUE);
    private final Try.CheckedFunction<Integer, String> f = Object::toString;
    private final Try.CheckedFunction<String, Integer> g = Integer::valueOf;

    @Test
    public void isFailure() throws Exception {
        assertThat(t.isFailure(), is(false));
    }

    @Test
    public void isSuccess() throws Exception {
        assertThat(t.isSuccess(), is(true));
    }

    @Test
    public void map() throws Exception {
        assertThat(t.map(f).map(g), is(t));
    }

    @Test
    public void flatMap() throws Exception {
        Try<String> t2 = t.flatMap(i -> Try.of(() -> f.apply(i)));

        assertThat(t2, is(Try.of(() -> "0")));
    }

    @Test
    public void recover() throws Exception {
        assertThat(t.recover(e -> 1), is(t));
    }

    @Test
    public void recoverWith() throws Exception {
        assertThat(t.recoverWith(e -> Try.of(() -> 1)), is(t));
    }

    @Test
    public void getOrElse() throws Exception {
        assertThat(t.getOrElse(() -> 1), is(STARTING_VALUE));
    }

    @Test
    public void filterNoMatch() throws Exception {
        Try<Integer> filtered = t.filter(i -> i == STARTING_VALUE + 1);

        assertThat(filtered.isFailure(), is(true));
        assertThat(filtered.getExceptionUnsafe(), instanceOf(NoSuchElementException.class));
    }

    @Test
    public void filterMathchesPredicate() throws Exception {
        assertThat(t.filter(i -> i == 0), is(t));
    }

    @Test
    public void filterCatchesThrowingPredicate() throws Exception {
        Try<Integer> filtered = t.filter(x -> {
            throw new RuntimeException("Failed recovery");
        });

        assertThat(filtered.isFailure(), is(true));
        assertThat(filtered.getExceptionUnsafe(), instanceOf(RuntimeException.class));
        assertThat(filtered.getExceptionUnsafe().getMessage(), is("Failed recovery"));
    }

    @Test
    public void fold() throws Exception {
        Integer i = t.fold(v -> {
            throw new RuntimeException();
        }, Function.identity());

        assertThat(i, is(STARTING_VALUE));
    }

    @Test
    public void getUnsafe() throws Exception {
        assertThat(t.getUnsafe(), is(STARTING_VALUE));
    }

    @Test
    public void getExceptionUnsafe() throws Exception {
        thrown.expect(NoSuchElementException.class);

        t.getExceptionUnsafe();
    }

    @Test
    public void toEither() throws Exception {
        assertThat(t.toEither(), is(Either.right(STARTING_VALUE)));
    }

    @Test
    public void toOption() throws Exception {
        assertThat(t.toOption(), is(Option.some(STARTING_VALUE)));
    }

}
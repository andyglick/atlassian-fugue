package io.atlassian.fugue;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryTest {

    private final Integer STARTING_VALUE = 1;
    private Try<Integer> successT = Try.of(() -> STARTING_VALUE);
    private Try<Integer> failT = Try.of(() -> {
        throw new IOException();
    });
    private Try.CheckedFunction<Integer, Try<String>> f = n -> Try.of(n::toString);
    private Try.CheckedFunction<String, Try<String>> g = s -> Try.of(s::toUpperCase);

    private Try.CheckedFunction<Integer, Try<String>> throwingF = n -> {
        throw new IOException();
    };

    private Function<Integer, Try<Integer>> unit = (Integer x) -> Try.of(() -> x);

    @Test
    public void leftIdentitySuccessCase() throws Throwable {
        assertThat(unit.apply(STARTING_VALUE).flatMap(f), is(f.apply(STARTING_VALUE)));
    }


    @Test
    public void leftIdentityKnownFailureCase() throws Throwable {
        Try<String> flatmapped = unit.apply(STARTING_VALUE).flatMap(throwingF);

        assertThat(flatmapped.isFailure(), is(true));
        assertThat(flatmapped.getExceptionUnsafe(), instanceOf(IOException.class));

        //unit(x).flatmap(f) != f(x), Does not hold due to unit catching exceptions
    }

    @Test
    public void rightIdentity() {
        assertThat(successT.flatMap(x -> unit.apply(x)), is(successT));
        assertThat(failT.flatMap(x -> unit.apply(x)), is(failT));
    }

    @Test
    public void associativitySuccessCase() {
        Try<String> lhs = successT.flatMap(f).flatMap(g);
        Try<String> rhs = successT.flatMap(x -> f.apply(x).flatMap(g));

        assertThat(lhs, is(rhs));
    }

    @Test
    public void associativityFailureCase() {
        Try<String> lhs = failT.flatMap(f).flatMap(g);
        Try<String> rhs = failT.flatMap(x -> f.apply(x).flatMap(g));

        assertThat(lhs, is(rhs));
    }

    @Test
    public void associativityExceptionThrowingCase() {
        Try<String> lhs = successT.flatMap(throwingF).flatMap(g);
        Try<String> rhs = successT.flatMap(x -> throwingF.apply(x).flatMap(g));

        assertThat(lhs.isFailure(), is(true));
        assertThat(rhs.isFailure(), is(true));
        assertThat(lhs.getExceptionUnsafe(), instanceOf(IOException.class));
        assertThat(rhs.getExceptionUnsafe(), instanceOf(IOException.class));
    }

    @Test
    public void sequenceReturnsFirstFailure() {
        Try<String> failed1 = Try.of(() -> {
            throw new RuntimeException("FIRST");
        });
        Try<String> failed2 = Try.of(() -> {
            throw new RuntimeException("SECOND");
        });
        Try<String> failed3 = Try.of(() -> {
            throw new RuntimeException("THIRD");
        });

        Try<Iterable<String>> result = Try.sequence(Arrays.asList(failed1, failed2, failed3));

        assertThat(result.isFailure(), is(true));
        assertThat(result.getExceptionUnsafe(), instanceOf(RuntimeException.class));
        assertThat(result.getExceptionUnsafe().getMessage(), is("FIRST"));
    }

    @Test
    public void sequenceReturnsValuesFromAllSuccesses() {
        Try<Iterable<Integer>> result = Try.sequence(IntStream.range(0, 10).mapToObj(i -> Try.of(() -> i))::iterator);

        assertThat(result.isSuccess(), is(true));
        assertThat(result.getUnsafe(), is(IntStream.range(0, 10).boxed().collect(toList())));
    }

}